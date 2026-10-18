package xc.crabapple.standardms.module.user;

import xc.crabapple.standardms.common.configuration.MM;
import xc.crabapple.standardms.common.domain.dao.UserDao;
import xc.crabapple.standardms.common.domain.dto.S2cDto;
import xc.crabapple.standardms.common.domain.dto.UserDto;
import org.springframework.stereotype.Service;
import xc.crabapple.core.cipher.sensitive.DesensitizationUtil;
import xc.crabapple.core.collection.ObjectUtil;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.json.jackson.JacksonUtil;
import xc.crabapple.ssm.mybatis.pagination.PaginationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * xuechong 2025-01-27
 */
@Service(value = "userServiceImpl")
public class UserServiceImpl {

    public S2cDto getList(String text) {
        Map<String, Object> paramMap = JacksonUtil.json2map(text);
        paramMap = PaginationUtil.append(paramMap);
        List<UserDao> list = MM.userMapper.selectList(paramMap);
        List<UserDto> userDtoList = new ArrayList<>();
        for (UserDao userDao : list) {
            UserDao desensitize = desensitize(userDao);
            UserDto userDto = new UserDto();
            ObjectUtil.merge(desensitize, userDto);
            userDtoList.add(userDto);
        }
        Integer total = PaginationUtil.getSize(list);
        S2cDto s2cDto = S2cDto.okay("查询成功");
        s2cDto.setParam("list", userDtoList);
        s2cDto.setParam("total", total);
        return s2cDto;
    }

    /**
     * 用户信息脱敏处理
     * @param userDao
     * @return
     */
    public UserDao desensitize(UserDao userDao) {
        UserDao result = ObjectUtil.clone(userDao);
        String mobile = result.getMobile();
        String account = result.getAccount();
        String email = result.getEmail();
        if (!StringUtil.isBlank(mobile)) {
            mobile = DesensitizationUtil.mobile(mobile);
            result.setMobile(mobile);
        }
        if (!StringUtil.isBlank(account)) {
            account = DesensitizationUtil.account(account);
            result.setAccount(account);
        }
        if (!StringUtil.isBlank(email)) {
            email = DesensitizationUtil.email(email);
            result.setEmail(email);
        }
        return result;
    }

}
