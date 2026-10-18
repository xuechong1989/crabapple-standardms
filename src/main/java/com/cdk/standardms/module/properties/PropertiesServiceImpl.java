package com.cdk.standardms.module.properties;

import com.cdk.standardms.common.configuration.MM;
import com.cdk.standardms.common.domain.dao.PropertiesDao;
import com.cdk.standardms.common.domain.dto.S2cDto;
import org.springframework.stereotype.Service;
import xc.crabapple.core.cipher.sm4.Sm4Util;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.json.jackson.JacksonUtil;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.core.regex.RegexUtil;
import xc.crabapple.ssm.mybatis.pagination.PaginationUtil;

import java.util.List;
import java.util.Map;

/**
 * xuechong 2025-01-27
 */
@Service(value="propertiesServiceImpl")
public class PropertiesServiceImpl {

    public S2cDto getList(String text) {
        Map<String, Object> paramMap = JacksonUtil.json2map(text);
        paramMap = PaginationUtil.append(paramMap);
        List<PropertiesDao> list = MM.propertiesMapper.selectList(paramMap);
        Integer total = PaginationUtil.getSize(list);
        S2cDto s2cDto = S2cDto.okay();
        s2cDto.setParam("list", list);
        s2cDto.setParam("total", total);
        return s2cDto;
    }

    public PropertiesDao getSysProperties(String alias){
        PropertiesDao sysProperties = MM.propertiesMapper.selectSysProperties(alias);
        if(sysProperties == null){
            return null;
        }
        String cipher = sysProperties.getCipher();
        if("yes".equals(cipher)){
            final String cipherKey = System.getenv("CRABAPPLE_PROPERTIES_SM4_KEY");
            final String cipherIv = System.getenv("CRABAPPLE_PROPERTIES_SM4_IV");
            if(StringUtil.isBlank(cipherKey) || StringUtil.isBlank(cipherIv)){
                LogUtil.error("PropertiesServiceImpl_getSysProperties_e1",
                        "t_properties 中存在 cipher=yes 的记录，但未配置 CRABAPPLE_PROPERTIES_SM4_KEY / CRABAPPLE_PROPERTIES_SM4_IV");
                return null;
            }
            final String cipherValue = sysProperties.getValue();
            final String plaintext = Sm4Util.decrypt(cipherKey, cipherIv, cipherValue, "CBC", 7);
            sysProperties.setValue(plaintext);
        }
        return sysProperties;
    }

    public String getString(String key){
        String value = getString(key, "");
        return value;
    }

    public String getString(String key, String defaultValue) {
        try {
            PropertiesDao propertiesDao = getSysProperties(key);
            if (propertiesDao == null) {
                return defaultValue;
            }
            String value = propertiesDao.getValue();
            return value;
        } catch (Throwable throwable) {
            LogUtil.error("PropertiesServiceImpl_getString_t1", throwable, key, defaultValue);
            return "";
        }
    }

    public Integer getInt(String key){
        Integer value = getInt(key, 0);
        return value;
    }

    public Integer getInt(String key, Integer defaultValue){
        String str = getString(key);
        if(StringUtil.isBlank(str)){
            return defaultValue;
        }
        boolean flag = RegexUtil.inNumber(str);
        if(!flag){
            return defaultValue;
        }
        //
        Integer value = Integer.valueOf(str);
        if(value != null){
            return value;
        }
        return defaultValue;
    }

    public Long getLong(String key){
        Long value = getLong(key, 0L);
        return value;
    }

    public Long getLong(String key, Long defaultValue){
        String str = getString(key);
        if(StringUtil.isBlank(str)){
            return defaultValue;
        }
        boolean flag = RegexUtil.inNumber(str);
        if(!flag){
            return defaultValue;
        }
        //
        Long value = Long.valueOf(str);
        if(value != null){
            return value;
        }
        return defaultValue;
    }

}