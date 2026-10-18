package com.cdk.standardms.module.login;

import com.cdk.standardms.common.configuration.MM;
import com.cdk.standardms.common.domain.dao.SmsDao;
import com.cdk.standardms.common.domain.dao.UserDao;
import com.cdk.standardms.common.domain.dto.S2cDto;
import com.cdk.standardms.common.domain.dto.UserDto;
import com.cdk.standardms.common.utils.AppLogUtil;
import com.cdk.standardms.common.utils.ContextUtil;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xc.crabapple.core.collection.CollectionUtil;
import xc.crabapple.core.collection.ObjectUtil;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.cipher.bcrypt.BcryptUtil;
import xc.crabapple.core.json.jackson.JacksonUtil;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.core.regex.RegexUtil;
import xc.crabapple.core.servlet.ServletUtil;
import xc.crabapple.core.sms.SmsData;
import xc.crabapple.core.sms.SmsUtil;
import xc.crabapple.core.sort.SortUtil;
import xc.crabapple.core.timer.TimerUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xuechong 2025-01-27
 */
@Service(value="loginServiceImpl")
public class LoginServiceImpl {

    public S2cDto register(String text) {
        UserDto userDto = JacksonUtil.json2bean(text, UserDto.class);
        if (userDto == null) {
            LogUtil.error("LoginServiceImpl_register_e34");
            S2cDto s2cDto = S2cDto.failure("参数错误！");
            return s2cDto;
        }
        String username = userDto.getUsername();
        String mobile = userDto.getMobile();
        String account = userDto.getAccount();
        String email = userDto.getEmail();
        String gender = userDto.getGender();
        String birthday = userDto.getBirthday();
        String password = userDto.getPassword();
        String repassword = userDto.getRepassword();
        String smsValidCode = userDto.getSmsValidCode();
        if (StringUtil.isBlank(username, mobile, account, email, gender, birthday, password, repassword, smsValidCode)) {
            LogUtil.error("LoginServiceImpl_register_e48");
            S2cDto s2cDto = S2cDto.failure("参数错误！");
            return s2cDto;
        }
        if (!repassword.equals(password)) {
            LogUtil.error("LoginServiceImpl_register_e53");
            S2cDto s2cDto = S2cDto.failure("两次输入的密码不一致！");
            return s2cDto;
        }
        List<UserDao> userDaoListByMobile = MM.userMapper.selectByMobile(mobile);
        boolean validMobile = RegexUtil.isMobile(mobile);
        if (!validMobile) {
            LogUtil.error("LoginServiceImpl_register_e61");
            S2cDto s2cDto = S2cDto.failure("手机号格式不正确！");
            return s2cDto;
        }
        if (userDaoListByMobile.size() > 0) {
            LogUtil.error("LoginServiceImpl_register_e66");
            S2cDto s2cDto = S2cDto.failure("该手机号已注册！");
            return s2cDto;
        }
        boolean validMail = RegexUtil.isEmail(email);
        if (!validMail) {
            LogUtil.error("LoginServiceImpl_register_e72");
            S2cDto s2cDto = S2cDto.failure("邮箱号格式不正确！");
            return s2cDto;
        }
        List<UserDao> userDaoListByMail = MM.userMapper.selectByEmail(email);
        if (!CollectionUtil.isBlank(userDaoListByMail)) {
            LogUtil.error("LoginServiceImpl_register_e78");
            S2cDto s2cDto = S2cDto.failure("该邮箱号已注册！");
            return s2cDto;
        }
        List<UserDao> userDaoList = MM.userMapper.selectByAccount(account);
        if (!CollectionUtil.isBlank(userDaoList)) {
            LogUtil.error("LoginServiceImpl_register_e87");
            S2cDto s2cDto = S2cDto.failure("该账号已注册！");
            return s2cDto;
        }
        S2cDto result = checkValidCode(mobile, smsValidCode);
        String success = result.getSuccess();
        if (!"yes".equals(success)) {
            LogUtil.error("LoginServiceImpl_register_e91");
            return result;
        }
        UserDao userDao = new UserDao();
        ObjectUtil.merge(userDto, userDao);
        // 口令只保存 BCrypt 摘要，禁止明文入库
        userDao.setPassword(BcryptUtil.getHash(password));
        userDao.setLoginCount(0);
        MM.userMapper.create(userDao);
        S2cDto s2cDto = S2cDto.okay("注册成功，请重新登录！");
        //插入日志
        Map<String, Object> map = new HashMap<>();
        map.put("account",userDao.getAccount());
        map.put("username",userDao.getUsername());
        map.put("mobile",userDao.getMobile());
        map.put("content","用户注册成功:"+JacksonUtil.obj2json(userDao));
        AppLogUtil.logInsert(ServletUtil.getRequest(), map);
        return s2cDto;
    }

    public S2cDto sendSms(String text) {
        ObjectNode objectNode = JacksonUtil.json2objectNode(text);
        String mobile = JacksonUtil.getString(objectNode, "mobile");
        String type = JacksonUtil.getString(objectNode, "type");
        List<UserDao> osUserList = MM.userMapper.selectByMobile(mobile);
        if(StringUtil.equals(type, "register")){
            if (!CollectionUtil.isBlank(osUserList)) {
                S2cDto s2cDto = S2cDto.failure("该手机号已经被注册");
                return s2cDto;
            }
        }
        if(StringUtil.equals(type, "login")){
            if (CollectionUtil.isBlank(osUserList)) {
                S2cDto s2cDto = S2cDto.failure("该手机号尚未注册！");
                return s2cDto;
            }
        }
        if (!CollectionUtil.isBlank(osUserList)) {
            if (osUserList.size() > 1) {
                S2cDto s2cDto = S2cDto.failure("存在多个重复的用户使用该手机号");
                return s2cDto;
            }
        }
        String validCode = p_getValidCode(mobile);
        if (validCode.length() == 6) {
            SmsData smsData = SmsUtil.send(mobile, validCode);
            boolean isSuccess = smsData.getSuccess();
            if (!isSuccess) {
                S2cDto s2cDto = p_getSendSmsFailureMsg(smsData, mobile);
                return s2cDto;
            }
            //插入日志
            Map<String, Object> map = new HashMap<>();
            map.put("content",mobile+"发送短信成功");
            AppLogUtil.logInsert(ServletUtil.getRequest(), map);
            S2cDto s2cDto = S2cDto.okay("短信验证码发送成功");
            return s2cDto;
        }
        SmsData smsTencent3Data = SmsUtil.send(mobile);
        boolean isSuccess = smsTencent3Data.getSuccess();
        if (!isSuccess) {
            S2cDto s2cDto = p_getSendSmsFailureMsg(smsTencent3Data, mobile);
            return s2cDto;
        }
        String validCode2 = smsTencent3Data.getValidCode();
        SmsDao smsValidCode = new SmsDao();
        smsValidCode.setMobile(mobile);
        smsValidCode.setValidCode(validCode2);
        int sm_codeTime_num = MM.propertiesService.getInt("sm_codeTime_num", 2);
        Date now = new Date();
        Long nowTimeLong = now.getTime();
        Long nowTimeLong01 = sm_codeTime_num * 1000 * 60 + nowTimeLong;
        Date validCodeTime = new Date(nowTimeLong01);
        smsValidCode.setValidCodeExp(TimerUtil.time2str(validCodeTime, TimerUtil.FORMATTYPE1));
        MM.smsService.create(smsValidCode);
        S2cDto s2cDto = S2cDto.okay("短信验证码发送成功");
        // 插入日志
        Map<String, Object> map = new HashMap<>();
        map.put("content",mobile+"发送短信成功");
        map.put("mobile",mobile);
        AppLogUtil.logInsert(ServletUtil.getRequest(), map);
        return s2cDto;
    }

    private synchronized String p_getValidCode(String mobile) {
        List<SmsDao> validCodeList = MM.smsMapper.selectListByMobile(mobile);
        if (CollectionUtil.isBlank(validCodeList)) {
            String msg = "error_1_" + mobile;
            return msg;
        }
        //
        List<String> tmpList = new ArrayList<>();
        for (SmsDao smsValidCode : validCodeList) {
            Date date = TimerUtil.str2time(smsValidCode.getValidCodeExp());  // 有效期至
            long validCodeTime = date.getTime();
            String validCode = smsValidCode.getValidCode();
            Long id = smsValidCode.getId();
            tmpList.add(validCodeTime + "," + validCode + "," + id);
        }
        // 倒序排列
        SortUtil.sortList(tmpList, null, "desc");
        // 删除冗余数据
        for (int i = 1; i < tmpList.size(); i++) {
            String tmp = tmpList.get(i);
            String[] array = StringUtil.split(tmp, ",");
            String id = array[2];
            MM.smsMapper.deleteById(Long.valueOf(id));
        }
        String tmp = tmpList.get(0);
        String[] array = StringUtil.split(tmp, ",");
        String validCodeTimeStr = array[0];
        long validCodeTime = Long.parseLong(validCodeTimeStr);
        long snow = TimerUtil.getStamp();
        if (validCodeTime < snow) {
            // 短信验证码已过期
            String msg = "error_2_" + validCodeTime + "_" + snow;
            return msg;
        }
        // 正常返回
        String validCode = array[1];
        return validCode;
    }

    private S2cDto p_getSendSmsFailureMsg(SmsData smsData, String mobile) {
        String code = smsData.getCode();
        String validCode = smsData.getValidCode();
        String msg = smsData.getMessage();
        String message = "短信验证码发送失败";
        if (StringUtil.containOf(code, "LimitExceeded")) {
            message = "操作过于频繁，请稍后重试";
        }
        LogUtil.errorNo("LoginServiceImpl_p_getSendSmsFailureMsg_1", message, code, msg, mobile);
        S2cDto s2cDto = S2cDto.failure(message);
        s2cDto.setParam("code", code);
        s2cDto.setParam("msg", msg);
        s2cDto.setParam("mobile", mobile);
        return s2cDto;
    }

    /**
     * 检测短信验证码是否正确
     */
    public S2cDto checkValidCode(String mobile, String smsValidCode) {
        if (StringUtil.isBlank(smsValidCode)) {
            S2cDto s2cDto = S2cDto.failure("请输入手机验证码！");
            return s2cDto;
        }
        SmsDao smsDao = MM.smsService.getByMobileAndCode(smsValidCode, mobile);
        if (smsDao == null) {
            S2cDto jsonResult = S2cDto.failure("手机号或验证码错误！请重新发送验证码");
            return jsonResult;
        }
        Date now = new Date();
        Long nowTimeLong01 = now.getTime();
        Long nowTimeLong02 = TimerUtil.str2time(smsDao.getValidCodeExp()).getTime();
        if (nowTimeLong01 > nowTimeLong02) {
            String nowTimeLong01Str = TimerUtil.getStrByStamp(nowTimeLong01, TimerUtil.FORMATTYPE1);
            String nowTimeLong02Str = TimerUtil.getStrByStamp(nowTimeLong02, TimerUtil.FORMATTYPE1);
            LogUtil.error("LoginServiceImpl_checkValidCode_1", nowTimeLong01Str, nowTimeLong02Str);
            MM.smsMapper.deleteById(smsDao.getId());
            S2cDto s2cDto = S2cDto.failure("手机号或验证码错误！请重新发送验证码");
            return s2cDto;
        }
        S2cDto s2cDto = S2cDto.okay("ok");
        return s2cDto;
    }

    public S2cDto loginBySms(String text, String smsValidCode) {
        UserDto userDto = JacksonUtil.json2bean(text, UserDto.class);
        String mobile = userDto.getMobile();
        if (StringUtil.isBlank(mobile, smsValidCode)) {
            return S2cDto.failure("请输入手机号和验证码！");
        }
        boolean validMobile = RegexUtil.isMobile(mobile);
        if (!validMobile) {
            return S2cDto.failure("手机号格式不正确！");
        }
        // 通过手机号查人
        List<UserDao> userDsoList = MM.userMapper.selectByMobile(mobile);
        if (CollectionUtils.isEmpty(userDsoList)) {
            LogUtil.error("LoginServiceImpl_cltdenglvByMobile_1", "系统中不存在该用户的手机号！", mobile);
            return S2cDto.failure("手机号或验证码错误");
        }
        // 校验：人是否存在
        if (userDsoList.size() != 1) {
            LogUtil.error("LoginServiceImpl_cltdenglvByMobile_2", "系统中存在多个用户使用手机号！", mobile);
            return S2cDto.failure("手机号或验证码错误");
        }
        UserDao userDao = userDsoList.get(0);
        // 验证码是否合法
        S2cDto smsJsonResult = checkValidCode(mobile, smsValidCode);
        String success = smsJsonResult.getSuccess();
        if (!"yes".equals(success)) {
            return S2cDto.failure("手机号或验证码错误");
        }
        // 登录成功失败次数归0
        userDao.setLoginCount(0);
        MM.userMapper.update(userDao);
        // 设置token
        final String token = ContextUtil.setCurrentUser(userDao);
        UserDao desensitizeUserDao = MM.userService.desensitize(userDao);
        UserDto desensitizeUserDto = new UserDto();
        ObjectUtil.merge(desensitizeUserDao, desensitizeUserDto);
        // 插入日志
        Map<String, Object> map = new HashMap<>();
        map.put("content","用户短信登录成功:"+mobile);
        map.put("mobile",mobile);
        AppLogUtil.logInsert(ServletUtil.getRequest(), map);
        // 手机号账号邮箱进行脱敏
        S2cDto s2cDto = S2cDto.okay("登录成功");
        s2cDto.setParam("token", token);
        s2cDto.setParam("desensitizeUserDto", desensitizeUserDto);
        return s2cDto;
    }

    public S2cDto loginByPwd(String text) {
        UserDto userDto = JacksonUtil.json2bean(text, UserDto.class);
        String mobile = userDto.getMobile();
        String account = userDto.getAccount();
        String password = userDto.getPassword();
        String email = userDto.getEmail();
        //判断用户到底使用账号密码还是邮箱登录
        UserDao userDao = new UserDao();
        boolean validMobile = RegexUtil.isMobile(mobile);
        if (validMobile) {
            List<UserDao> list = MM.userMapper.selectByMobile(mobile);
            if (list != null && list.size() == 1) {
                userDao = list.get(0);
            }
        }
        if(StringUtil.isBlank(email)){
            email = mobile;
        }
        boolean validMail = RegexUtil.isEmail(email);
        if (validMail) {
            List<UserDao> list = MM.userMapper.selectByEmail(email);
            if (list != null && list.size() == 1) {
                userDao = list.get(0);
            }
        }
        if (!validMail && !validMobile) {
            if(StringUtil.isBlank(account)){
                account = mobile;
            }
            List<UserDao> list = MM.userMapper.selectByAccount(account);
            if (list != null && list.size() == 1) {
                userDao = list.get(0);
            }else{
                LogUtil.warn("LoginServiceImpl_login_23", "用户名或者口令错误，登录失败", account);
                return S2cDto.failure("用户名或者口令错误，登录失败！");
            }
        }
        if (userDao == null || userDao.getId() == null) {
            LogUtil.warn("LoginServiceImpl_login_2", "请输入正确的手机号或邮箱或用户名或密码");
            return S2cDto.failure("用户名或者口令错误，登录失败！");
        }
        String dbPassword = userDao.getPassword(true);
        if (StringUtil.isBlank(dbPassword) || !BcryptUtil.check(password, dbPassword)) {
            // 用户登录失败次数+1,三次后只能验证码登录
            Integer loginCount = userDao.getLoginCount();
            loginCount = loginCount + 1;
            userDao.setLoginCount(loginCount);
            MM.userMapper.update(userDao);
            LogUtil.warn("LoginServiceImpl_login_3", "请输入正确的手机号或邮箱或用户名或密码");
            return S2cDto.failure("用户名或者口令错误，登录失败！");
        }
        // 校验是否允许登录
        Integer loginCount = userDao.getLoginCount();
        if (loginCount == null) {
            loginCount = 0;
        }
        if (loginCount >= 3) {
            LogUtil.error("LoginServiceImpl_login_e356", "用户仅支持短信验证码登录");
            return S2cDto.failure("登录错误次数超过3次，请使用短信验证码登录！");
        }
        //登录成功失败次数归0
        userDao.setLoginCount(0);
        MM.userMapper.update(userDao);
        // 设置token
        final String token = ContextUtil.setCurrentUser(userDao);
        // 手机号账号邮箱进行脱敏
        UserDao desensitizeUserDao = MM.userService.desensitize(userDao);
        UserDto desensitizeUserDto = new UserDto();
        ObjectUtil.merge(desensitizeUserDao, desensitizeUserDto);
        // 插入日志
        Map<String, Object> map = new HashMap<>();
        map.put("account", userDao.getAccount());
        map.put("username", userDao.getUsername());
        map.put("mobile", userDao.getMobile());
        map.put("content","用户账号密码登录成功:"+mobile);
        AppLogUtil.logInsert(ServletUtil.getRequest(), map);
        S2cDto s2cDto = S2cDto.okay("登录成功");
        s2cDto.setParam("token", token);
        s2cDto.setParam("desensitizeUserDto", desensitizeUserDto);
        return s2cDto;
    }

    public S2cDto logout() {
        //插入日志
        Map<String, Object> map = new HashMap<>();
        map.put("content","用户退出成功:"+ContextUtil.getCurrentUser().getMobile());
        map.put("mobile",ContextUtil.getCurrentUser().getMobile());
        AppLogUtil.logInsert(ServletUtil.getRequest(), map);
        ContextUtil.removeCurrentUser();
        S2cDto s2cDto = S2cDto.okay("退出登录成功");
        return s2cDto;
    }

}