package com.cdk.standardms.common.utils;

import com.cdk.standardms.common.configuration.MM;
import com.cdk.standardms.common.domain.dao.UserDao;
import com.cdk.standardms.common.domain.dco.ContextDco;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.cookie.CookieBean;
import xc.crabapple.core.cookie.CookieUtil;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.core.net.ip.IPUtil;
import xc.crabapple.core.random.RandomUtil;
import xc.crabapple.core.servlet.ServletUtil;
import xc.crabapple.core.thread.ThreadUtil;
import xc.crabapple.core.timer.TimerUtil;
import xc.crabapple.ssm.cache.CacheUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xuechong 2025-01-27
 */
public class ContextUtil {

    // chenzhaoyi 2023-08-29 登录超时配置
    // xuechong 2024-07-17 登录超时配置更新
    public static Long loginTimeLimited = 1576800000L;  // 登录时间限制，单位秒。 (3600 * 24 * 365) * 50 = 1576800000秒 = 50年
    public static Integer lastReqTimeLimited = 1576800000;  // 未操作时间限制，单位秒。 (3600 * 24 * 365) * 50 = 1576800000秒 = 50年

    // yaobin 2023-04-18 ContextSession里面的东西
    private static Map<String, Object> p_sessionMap = null;
    // xuechong 2023-02-21 虚拟用户
    private static Map<String, UserDao> p_virtualUserMap = null;
    // xuechong 2023-08-04 存储userId到user对象的映射
    private static Map<Long, UserDao> p_userMap = null;

    /**
     * 初始化虚拟用户、用于第三方系统接口对接
     */
    public static final void init() {
        loginTimeLimited = MM.propertiesService.getLong("loginTimeLimited", loginTimeLimited);
        lastReqTimeLimited = MM.propertiesService.getInt("lastReqTimeLimited", lastReqTimeLimited);
        p_sessionMap = new ConcurrentHashMap<>(1024);
        p_userMap = new ConcurrentHashMap<>(512);
    }

    /**
     * 登录超时配置
     */
    public static final void tokenMonitor() {
        loginTimeLimited = MM.propertiesService.getLong("loginTimeLimited", loginTimeLimited);
        lastReqTimeLimited = MM.propertiesService.getInt("lastReqTimeLimited", lastReqTimeLimited);
        //
        List<String> tokenList = getContextDcoTokenList();
        for (String token : tokenList) {
            ContextDco contextDco = getContextDco(token);
            if (contextDco == null) {
                return;
            }
            final Long userId = contextDco.getUserId();
            //
            long currentTimeMillis = System.currentTimeMillis();  // 当前时间，毫秒时间戳
            //
            String loginTimeFormat = contextDco.getLoginTime();  // 最后登录时间
            long loginTime = TimerUtil.str2time(loginTimeFormat).getTime();  // 最后登录时间，毫秒时间戳
            if ((currentTimeMillis - loginTime) > (loginTimeLimited * 1000)) {
                LogUtil.warn("ContextUtil_tokenMonitor_1", "用户已过期=" + contextDco.getUserId(), "最近登录时间=" + contextDco.getLoginTime());
                removeCurrentUser(token);
                continue;
            }
            //
            String lastReqTimeFormat = contextDco.getLastReqTime();  // 最后请求时间
            long lastReqTime = TimerUtil.str2time(lastReqTimeFormat).getTime();
            if ((currentTimeMillis - lastReqTime) > (lastReqTimeLimited * 1000)) {
                LogUtil.warn("ContextUtil_tokenMonitor_2", "当期用户已过期=" + contextDco.getUserId(), "最近请求时间=" + contextDco.getLoginTime(), "当前时间=" + currentTimeMillis);
                removeCurrentUser(token);
            }
        }
    }

    /**
     * xuechong 2023-02-21
     * 获得虚拟用户
     */
    public static final UserDao getVirtualUser(final String paramToken) {
        if (StringUtil.isBlank(paramToken)) {
            return null;
        }
        UserDao userDao = p_virtualUserMap.get(paramToken);
        if (userDao == null) {
            return null;
        }
        //
        return userDao;
    }

    /**
     * 获取token
     */
    public static final String getToken() {
        //
        String threadToken = getThreadToken();
        if (!StringUtil.isBlank(threadToken)) {
            return threadToken;
        }
        //
        String cookieToken = getCookieToken();
        if (!StringUtil.isBlank(cookieToken)) {
            return cookieToken;
        }
        //
        String paramToken = getParamToken();
        if (!StringUtil.isBlank(paramToken)) {
            return paramToken;
        }
        return "";
    }

    /**
     * 获取系统子线程中的token
     */
    public static final String getThreadToken() {
        Object object = ThreadUtil.getData("token");
        if (object != null) {
            String threadToken = object.toString();
            return threadToken;
        }
        return "";
    }

    /**
     * 通过客户端的cookie(httponly)获取token
     */
    public static final String getCookieToken() {
        final String tokenPreKey = CacheUtil.getStandardKey("token");
        String cookieToken = CookieUtil.get(tokenPreKey);
        if (!StringUtil.isBlank(cookieToken)) {
            return cookieToken;
        }
        return "";
    }

    /**
     * xuechong 2023-09-24
     * 通过客户端传参获取token
     */
    public static final String getParamToken() {
        String cipherParamToken = ServletUtil.getString("token");
        if (!StringUtil.isBlank(cipherParamToken)) {
            return cipherParamToken;
        }
        final String tokenPreKey = CacheUtil.getStandardKey("token");
        cipherParamToken = ServletUtil.getString(tokenPreKey);
        if (!StringUtil.isBlank(cipherParamToken)) {
            return cipherParamToken;
        }
        return "";
    }

    public static final String setCurrentUser(final UserDao UserDao) {
        final String clientId = ServletUtil.getString("clientId");
        final String clientIp =  IPUtil.getClientIp();
        final String userAgent = ServletUtil.getUserAgent();
        //
        String token = setCurrentUser(UserDao, clientId, clientIp, userAgent, true);
        return token;
    }

    /**
     * yaobin 2024-08-15 禅道需求-222-合肥水源项目对接建管系统平台单点登录 更新登录缓存用户的信息
     *
     * @param osUser
     * @return
     */
    public static final void updateCurrentUser(final UserDao osUser) {
        Long userId = osUser.getId();
        p_userMap.put(userId, osUser);
    }

    /**
     * @param clientId
     * @param clientIp
     * @param userAgent
     * @param isAddCookie 是否添加cookie用于启动项目初始化时可以生成一个固定token以及绑定用户
     */
    public static final String setCurrentUser(final UserDao userDao, final String clientId, final String clientIp, final String userAgent, boolean isAddCookie) {
        ContextDco contextDco = new ContextDco();
        contextDco.setClientId(clientId);
        contextDco.setUserId(userDao.getId());
        contextDco.setAccount(userDao.getAccount());
        contextDco.setUsername(userDao.getUsername());
        contextDco.setClientIp(clientIp);
        contextDco.setUserAgent(userAgent);
        String dateStr = TimerUtil.time2str(new Date());
        contextDco.setLoginTime(dateStr);
        contextDco.setLastReqTime(dateStr);
        final String token = RandomUtil.getStrWithStamp(128, false);
        p_setCurrentUser(token, contextDco, userDao, isAddCookie);
        return token;
    }

    public static final String setCurrentUser(final String token, final UserDao user) {
        p_setCurrentUser(token, null, user, true);
        return token;
    }

    /**
     * xuechong 2023-08-04
     * yaobin 2024-04-25 新增参数是否添加cookie
     * @param token       128位token
     * @param contextDco
     * @param isAddCookie 是否添加cookie用于启动项目初始化时可以生成一个固定token以及绑定用户
     */
    private static final void p_setCurrentUser(final String token, final ContextDco contextDco, final UserDao userDao, boolean isAddCookie) {
        if (StringUtil.isBlank(token)) {
            return;
        }
        if (contextDco == null) {
            return;
        }
        if (userDao == null) {
            return;
        }
        // 建立 OsUser与ContextDco之间的关联关系
        final Long userId = contextDco.getUserId();
        p_userMap.put(userId, userDao);
        final String tokenKey = "token_" + token;
        CacheUtil.set(tokenKey, contextDco, loginTimeLimited);
        //
        if (isAddCookie) {
            // 存入浏览器cookie
            final String tokenPreKey = CacheUtil.getStandardKey("token");
            CookieBean cookieBean = new CookieBean();
            cookieBean.setName(tokenPreKey);
            cookieBean.setValue(token);
            cookieBean.setHttpOnly(true);
            cookieBean.setMaxAge(loginTimeLimited);
            CookieUtil.add(cookieBean);
            //
            ServletUtil.setAttribute("cookieToken", token);
        }
    }

    public static final void setContextDco(final String token, final ContextDco contextDco) {
        if (StringUtil.isBlank(token) || contextDco == null) {
            return;
        }
        // 存入后端缓存
        final String tokenKey = "token_" + token;
        CacheUtil.set(tokenKey, contextDco, 3600 * 24 * 365 * 100 * 1000);
    }

    /**
     * chenzhaoyi 2023-08-29
     * 设置最后一次请求时间
     */
    public static final UserDao getCurrentUser() {
        String token = getToken();
        if (StringUtil.isBlank(token)) {
            // LogUtil.errorNo("ContextUtil_getCurrentUser_1", "获取用户失败，token为空", token);
            return null;
        }
        UserDao userDao = p_getCurrentUser(token);
        // yaoxiaowen 2023-09-24 18:07:35 从url获取token
        if (userDao == null) {
            userDao = ContextUtil.getCurrentUserByParamToken();
        }
        return userDao;
    }

    /**
     * xuechong 2023-09-24
     * 通过客户端传参传过来的token获取user对象
     */
    public static final UserDao getCurrentUserByParamToken() {
        final String paramToken = getParamToken();
        UserDao userDao = p_getCurrentUser(paramToken);
        return userDao;
    }

    private static UserDao p_getCurrentUser(String token) {
        ContextDco contextDco = getContextDco(token);
        if (contextDco == null) {
             LogUtil.error("ContextUtil_getCurrentUser_2", "没有token对应的用户");
            return null;
        }
        final Long userId = contextDco.getUserId();
        final UserDao userDao = p_userMap.get(userId);
        if (userDao == null) {
            return null;
        }
        return userDao;
    }

    public static final ContextDco getContextDco(String token) {
        final String tokenKey = "token_" + token;
        ContextDco contextDco = CacheUtil.get(tokenKey);
        // LogUtil.errorNo("ContextUtil_getCurrentUser_2", "没有token对应的用户", paramToken);
        return contextDco;
    }

    /**
     * xuechong 2023-07-21 add
     */
    public static final void removeCurrentUser() {
        final String token = getToken();
        removeCurrentUser(token);
    }

    /**
     * xuechong 2023-07-21 add
     *
     * @param token
     */
    public static final void removeCurrentUser(final String token) {
        final String tokenPreKey = CacheUtil.getStandardKey("token");
        CookieUtil.remove(tokenPreKey);
        final String tokenKey = "token_" + token;
        ContextDco contextDco = CacheUtil.get(tokenKey);
        if (contextDco != null) {
            Long userId = contextDco.getUserId();
            p_userMap.remove(userId);
        }
        CacheUtil.remove(tokenKey);
    }

    public static final Long getCurrentUserId() {
        UserDao userDao = getCurrentUser();
        if (userDao == null) {
            return null;
        }
        final Long userId = userDao.getId();
        return userId;
    }

    /**
     * 返回所有ContextDco
     */
    public static final List<String> getContextDcoTokenList() {
        final String tokenPreKey = CacheUtil.getStandardKey("token");
        final String prefixKey = tokenPreKey + "_";
        List<String> tokenList = CacheUtil.getLocalCacheKeyList();
        List<String> resultList = new ArrayList<>();
        for (String token : tokenList) {
            if (StringUtil.isBlank(token)) {
                continue;
            }
            if (token.startsWith(prefixKey)) {
                String realToken = token.substring(prefixKey.length());
                resultList.add(realToken);
            }
        }
        return resultList;
    }

    /**
     * yaobin 2023-07-28 判断当前用户是否已登录
     */
    public static final Boolean isLoginedUser() {
        String paramToken = getToken();
        if (!StringUtil.isBlank(paramToken)) {
            UserDao userDao = getCurrentUser();
            if (userDao != null) {
                return true;
            }
        }
        return false;
    }

}