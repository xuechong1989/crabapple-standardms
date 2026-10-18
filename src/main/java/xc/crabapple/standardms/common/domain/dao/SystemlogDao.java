package xc.crabapple.standardms.common.domain.dao;

import xc.crabapple.core.random.SnowflakeUtil;
import xc.crabapple.core.timer.TimerUtil;

public class SystemlogDao extends AppDao{

    private Long id;
    private String module;
    private String operation;
    private String account;
    private String username;
    private String mobile;
    private String content;
    private String apiName;
    private String apiParam;
    private String clientip;
    private String userAgent;
    private String createTime;
    private Long createUserId;

    public SystemlogDao() {
        id = SnowflakeUtil.getLong();
        createTime = TimerUtil.now();
    }

    /**
     * 获取
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取
     * @return module
     */
    public String getModule() {
        return module;
    }

    /**
     * 设置
     * @param module
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * 获取
     * @return subModule
     */
    public String getOperation() {
        return operation;
    }

    /**
     * 设置
     * @param operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * 获取
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取
     * @return mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置
     * @param mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取
     * @return action
     */
    public String getApiName() {
        return apiName;
    }

    /**
     * 设置
     * @param apiName
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiParam() {
        return apiParam;
    }

    public void setApiParam(String apiParam) {
        this.apiParam = apiParam;
    }

    /**
     * 获取
     * @return ip
     */
    public String getClientip() {
        return clientip;
    }

    /**
     * 设置
     * @param clientip
     */
    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    /**
     * 获取
     * @return userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 设置
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 获取
     * @return createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置
     * @param createTime
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取
     * @return createBy
     */
    public Long getCreateUserId() {
        return createUserId;
    }

    /**
     * 设置
     * @param createUserId
     */
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

}