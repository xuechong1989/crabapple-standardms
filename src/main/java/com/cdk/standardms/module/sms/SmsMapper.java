package com.cdk.standardms.module.sms;

import com.cdk.standardms.common.domain.dao.SmsDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SmsMapper {

    // ########## select
    List<SmsDao> selectListByMobile(@Param("mobile") String mobile);

    SmsDao selectByMobileAndCode(String validCode, String mobile);



    // ########## insert

    void create(SmsDao smsDao);


    // ########## update

    // ########## delete

    void deleteById(@Param("id") Long id);

}