package com.cdk.standardms.module.systemlog;

import com.cdk.standardms.common.domain.dao.SystemlogDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface SystemlogMapper {

    // ########## insert
    void create(SystemlogDao systemlogDao);



    // ########## select
    List<SystemlogDao> selectList(Map<String, Object> paramMap);



    // ########## update



    // ########## delete


}