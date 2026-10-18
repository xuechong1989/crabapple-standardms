package com.cdk.standardms.common.domain.dco;

import xc.crabapple.core.data.dco.DcoBase;

/**
 * xuechong 2023-08-04
 * xuechong 2023-09-08 添加客户端标志位clientId字段
 * 用户上下文DCO类
 */
public class ContextDco extends DcoBase {

    private Long userId ;
    private String account = "";
    private String username = "";
    private String clientId = "";
    private String clientIp = "";
    private String userAgent = "";
    private String loginTime = "";  // 登录时间点，精确到毫秒
    private String lastReqTime = "";  // 最近一次接收客户端请求的时间点，精确到毫秒
    private String status = "normal";  // 当前登录状态

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLastReqTime() {
        return lastReqTime;
    }

    public void setLastReqTime(String lastReqTime) {
        this.lastReqTime = lastReqTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}