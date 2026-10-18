package com.cdk.standardms.module.sms;

import com.cdk.standardms.common.configuration.MM;
import com.cdk.standardms.common.domain.dao.SmsDao;
import org.springframework.stereotype.Service;
import xc.crabapple.core.random.SnowflakeUtil;

@Service(value = "smsServiceImpl")
public class SmsServiceImpl {

    public void create(SmsDao smsDao) {
        Long id = smsDao.getId();
        if (id==null) {
            id = SnowflakeUtil.getLong();
            smsDao.setId(id);
        }
        MM.smsMapper.create(smsDao);
    }

    public SmsDao getByMobileAndCode(String validCode, String mobile) {
        SmsDao smsDao = MM.smsMapper.selectByMobileAndCode(validCode,mobile);
        return smsDao;
    }

}