package xc.crabapple.standardms.common.utils;

import xc.crabapple.standardms.common.configuration.MM;
import xc.crabapple.standardms.common.domain.dao.SystemlogDao;
import xc.crabapple.standardms.common.domain.dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.file.properties.PropertiesUtil;
import xc.crabapple.core.log.LogConfigBean;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.core.servlet.ServletUtil;
import xc.crabapple.core.thread.Business;
import xc.crabapple.core.thread.ThreadUtil;
import xc.crabapple.core.timer.TimerUtil;

import java.util.Map;

/**
 * xuechong 2025-01-09
 */
public class AppLogUtil {

    /**
     * xuechong 2025-01-09
     */
    public static final void init(){
        LogConfigBean logConfigBean = new LogConfigBean();
        logConfigBean.setType(PropertiesUtil.getValue("log.type"));
        logConfigBean.setLevel(PropertiesUtil.getValue("log.level"));
        logConfigBean.setPath(PropertiesUtil.getValue("log.file.path"));
        logConfigBean.setMaxHistory(PropertiesUtil.getInt("log.file.maxHistory"));
        logConfigBean.setMaxFileSize(PropertiesUtil.getValue("log.file.maxFileSize"));
        LogUtil.init(logConfigBean);
    }

    /**
     * 每次调用接口都会执行该方法
     * 标准做法是单独启动一个新子线程处理（使用线程池工具）
     */
    public static void logInsert(HttpServletRequest request, Map<String, Object> map) {
        // 当前操作人
        UserDao user = ContextUtil.getCurrentUser();
        String clientIp = ServletUtil.getClientIp();
        ThreadUtil.start(new Business("BpmTaskServiceImpl_logInsert_1", false) {
            @Override
            public void process() {
                final String requestURI = ServletUtil.getRequestURI(request);
                if (StringUtil.isBlank(requestURI)) {
                    return;
                }
                SystemlogDao systemlogDao = new SystemlogDao();
                // 参数信息
                if ("/api/crabapple/standardms/login/loginByPwd".equals(requestURI)) {
                    systemlogDao.setModule("登录");
                    systemlogDao.setOperation("账号密码登录");
                    systemlogDao.setAccount(map.get("account").toString());
                    systemlogDao.setUsername(map.get("username").toString());
                    systemlogDao.setMobile(map.get("mobile").toString());
                    systemlogDao.setContent(map.get("content").toString());
                } else if ("/api/crabapple/standardms/login/loginBySms".equals(requestURI)) {
                    systemlogDao.setModule("登录");
                    systemlogDao.setOperation("短信登录");
                    systemlogDao.setMobile(map.get("mobile").toString());
                    systemlogDao.setContent(map.get("content").toString());
                } else if ("/api/crabapple/standardms/login/register".equals(requestURI)) {
                    systemlogDao.setModule("用户管理");
                    systemlogDao.setOperation("用户注册");
                    systemlogDao.setAccount(map.get("account").toString());
                    systemlogDao.setUsername(map.get("username").toString());
                    systemlogDao.setMobile(map.get("mobile").toString());
                    systemlogDao.setContent(map.get("content").toString());
                } else if ("/api/crabapple/standardms/login/logout".equals(requestURI)) {
                    systemlogDao.setModule("用户管理");
                    systemlogDao.setOperation("用户退出");
                    systemlogDao.setContent(map.get("content").toString());
                } else if ("/api/crabapple/standardms/login/sendSms".equals(requestURI)) {
                    systemlogDao.setModule("用户管理");
                    systemlogDao.setOperation("发送短信");
                    systemlogDao.setMobile(map.get("mobile").toString());
                    systemlogDao.setContent(map.get("content").toString());
                }
                String userAgent = request.getHeader("user-agent");
                if (user != null) {
                    systemlogDao.setAccount(user.getAccount());
                    systemlogDao.setUsername(user.getUsername());
                    systemlogDao.setMobile(user.getMobile());
                    systemlogDao.setCreateUserId(user.getId());
                }
                systemlogDao.setApiName(requestURI);
                systemlogDao.setClientip(clientIp);
                systemlogDao.setUserAgent(userAgent);
                systemlogDao.setCreateTime(TimerUtil.time2str());
                MM.systemlogMapper.create(systemlogDao);
            }
        });
    }

}
