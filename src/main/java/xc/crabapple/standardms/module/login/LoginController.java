package xc.crabapple.standardms.module.login;

import xc.crabapple.standardms.common.configuration.MM;
import xc.crabapple.standardms.common.domain.dto.S2cDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xc.crabapple.core.net.http.ContentTypeUtil;

/**
 * xuechong 2025-02-12
 * 登录模块
 */
@Controller
@RequestMapping("/api/crabapple/standardms/login")
public class LoginController {

    /**
     * 发送短信
     */
    @RequestMapping(value = "/sendSms", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto sendSms(String text) {
        S2cDto s2cDto = MM.loginService.sendSms(text);
        return s2cDto;
    }

    /**
     * 普通用户注册
     */
    @RequestMapping(value = "/register", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto register(String text) {
        S2cDto s2cDto = MM.loginService.register(text);
        return s2cDto;
    }

    /**
     * 用户短信验证码登录
     */
    @RequestMapping(value = "/loginBySms", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto loginBySms(String text, String smsValidCode) {
        S2cDto s2cDto = MM.loginService.loginBySms(text, smsValidCode);
        return s2cDto;
    }

    /**
     * 用户
     */
    @RequestMapping(value = "/loginByPwd", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto login(String text) {
        S2cDto s2cDto = MM.loginService.loginByPwd(text);
        return s2cDto;
    }

    @RequestMapping(value="/logout", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto logout() {
        S2cDto s2cDto = MM.loginService.logout();
        return s2cDto;
    }

}