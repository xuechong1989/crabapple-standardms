package com.cdk.standardms.module.systemlog;

import com.cdk.standardms.common.configuration.MM;
import com.cdk.standardms.common.domain.dao.SystemlogDao;
import com.cdk.standardms.common.domain.dto.S2cDto;
import org.springframework.stereotype.Service;
import xc.crabapple.core.json.jackson.JacksonUtil;
import xc.crabapple.ssm.mybatis.pagination.PaginationUtil;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service(value = "systemlogServiceImpl")
public class SystemlogServiceImpl {

    public S2cDto getList(String text) {
        Map<String, Object> paramMap = JacksonUtil.json2map(text);
        paramMap = PaginationUtil.append(paramMap);
        List<SystemlogDao> list = MM.systemlogMapper.selectList(paramMap);
        Integer total = PaginationUtil.getSize(list);
        S2cDto s2cDto = S2cDto.okay();
        s2cDto.setParam("list", list);
        s2cDto.setParam("total", total);
        return s2cDto;
    }

}