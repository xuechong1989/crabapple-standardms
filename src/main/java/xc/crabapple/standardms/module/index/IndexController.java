package xc.crabapple.standardms.module.index;

import xc.crabapple.standardms.common.domain.dto.S2cDto;
import xc.crabapple.standardms.common.configuration.MM;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xc.crabapple.core.net.http.ContentTypeUtil;

@Controller
@RequestMapping("/api/crabapple/standardms/index")
public class IndexController {

    /**
     * xuechong 2025-01-22
     * 系统第一个接口
     * http://127.0.0.1:9089/
     * 推荐：默认只使用POST请求
     */
    @RequestMapping(value = "/main", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto main() {
        S2cDto s2cDto = MM.indexService.main();
        return s2cDto;
    }

    /**
     * xuechong 2025-01-22
     * 系统第一个接口
     * http://127.0.0.1:9089/
     * 推荐：默认只使用POST请求
     */
    @RequestMapping(value = "/heart", method={RequestMethod.POST}, produces={ContentTypeUtil.application_json})
    @ResponseBody
    public S2cDto heart() {
        S2cDto s2cDto = MM.indexService.heart();
        return s2cDto;
    }

}
