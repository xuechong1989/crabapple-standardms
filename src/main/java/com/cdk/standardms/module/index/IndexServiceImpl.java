package com.cdk.standardms.module.index;

import com.cdk.standardms.common.domain.dto.S2cDto;
import org.springframework.stereotype.Service;
import xc.crabapple.core.timer.TimerUtil;
import xc.crabapple.ssm.security.SecurityUtil;
import xc.crabapple.ssm.security.webshell.WebshellUtil;

import java.util.Map;

/**
 * xuechong 2025-01-27
 */
@Service(value="indexServiceImpl")
public class IndexServiceImpl {

    public S2cDto main() {
        String csrfToken = SecurityUtil.csrfToken();
        Map<String, String> fileTypeMappingMap = WebshellUtil.revertFileTypeMappingMap();
        //
        S2cDto s2cDto = S2cDto.okay("success");
        s2cDto.setParam("csrfToken", csrfToken);
        s2cDto.setParam("fileTypeMapping", fileTypeMappingMap);
        return s2cDto;
    }

    public S2cDto heart() {
        final String now = TimerUtil.now();
        S2cDto s2cDto = S2cDto.okay("success");
        s2cDto.setParam("now", now);
        return s2cDto;
    }

}