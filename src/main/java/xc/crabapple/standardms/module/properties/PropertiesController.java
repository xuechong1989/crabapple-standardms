package xc.crabapple.standardms.module.properties;

import xc.crabapple.standardms.common.configuration.MM;
import xc.crabapple.standardms.common.domain.dto.S2cDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xc.crabapple.core.net.http.ContentTypeUtil;

/**
 * 系统参数控制器
 */
@Controller
@RequestMapping(value = "/api/crabapple/standardms/properties")
public class PropertiesController {

    @RequestMapping(value = "/getList", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto getList(String text) {
        S2cDto s2cDto = MM.propertiesService.getList(text);
        return s2cDto;
    }

}