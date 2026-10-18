package com.cdk.standardms.module.properties;

import com.cdk.standardms.common.domain.dao.PropertiesDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface PropertiesMapper {


    // ########## select
    List<PropertiesDao> selectList(Map<String, Object> paramMap);
    PropertiesDao selectSysProperties(@Param("key") String key);


    // ########## insert


    // ########## update


    // ########## delete


}