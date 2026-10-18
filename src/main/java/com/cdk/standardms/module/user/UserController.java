package com.cdk.standardms.module.user;

import com.cdk.standardms.common.configuration.MM;
import com.cdk.standardms.common.domain.dto.S2cDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xc.crabapple.core.net.http.ContentTypeUtil;

/**
 * xuechong 2024-06-19
 */
@Controller
@RequestMapping("/api/crabapple/standardms/user")
public class UserController {

    /**
     * 获取用户列表
     */
    @RequestMapping(value="/getList", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto getList(String text) {
        S2cDto s2cDto = MM.userService.getList(text);
        return s2cDto;
    }

}
