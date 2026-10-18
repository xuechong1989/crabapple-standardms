package com.cdk.standardms.common.domain.dao;

import xc.crabapple.core.random.SnowflakeUtil;
import xc.crabapple.core.timer.TimerUtil;

public class SmsDao extends AppDao {

    public Long id;  // 主键
    public String mobile;  // 手机号码
    public String validCode;  // 验证码
    public String validCodeExp;  // 验证码有效时间（单位分）
    private String createTime;  // 创建时间
    private String createUserId;  // 创建人ID或标识
    private String updateTime;  // 更新时间
    private String updateUserId;  // 更新人ID或标识

    public SmsDao() {
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
     * @return phoneNumber
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
     * @return validCode
     */
    public String getValidCode() {
        return validCode;
    }

    /**
     * 设置
     * @param validCode
     */
    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    /**
     * 获取
     * @return validCodeTime
     */
    public String getValidCodeExp() {
        return validCodeExp;
    }

    /**
     * 设置
     * @param validCodeExp
     */
    public void setValidCodeExp(String validCodeExp) {
        this.validCodeExp = validCodeExp;
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
     * @return updateTime
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置
     * @param updateTime
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取
     * @return createBy
     */
    public String getCreateUserId() {
        return createUserId;
    }

    /**
     * 设置
     * @param createUserId
     */
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 获取
     * @return updateBy
     */
    public String getUpdateUserId() {
        return updateUserId;
    }

    /**
     * 设置
     * @param updateUserId
     */
    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

}