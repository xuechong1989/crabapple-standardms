package xc.crabapple.standardms.module.systemlog;

import xc.crabapple.standardms.common.configuration.MM;
import xc.crabapple.standardms.common.domain.dto.S2cDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xc.crabapple.core.net.http.ContentTypeUtil;

@Controller
@RequestMapping("/api/crabapple/standardms/systemlog")
public class SystemlogController {

    @RequestMapping(value = "/getList", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto getList(String text) {
        S2cDto s2cDto = MM.systemlogService.getList(text);
        return s2cDto;
    }

}
