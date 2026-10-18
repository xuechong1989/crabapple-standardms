package xc.crabapple.standardms;

import xc.crabapple.standardms.common.configuration.MM;
import xc.crabapple.standardms.common.utils.AppCacheUtil;
import xc.crabapple.standardms.common.utils.AppEnvUtil;
import xc.crabapple.standardms.common.utils.AppLogUtil;
import xc.crabapple.standardms.common.utils.AppScheduleUtil;
import xc.crabapple.standardms.common.utils.AppSecurityUtil;
import xc.crabapple.standardms.module.sms.AppSmsUtil;
import xc.crabapple.standardms.common.utils.ContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import xc.crabapple.springboot.SpringbootUtil;
import xc.crabapple.ssm.spring.SpringUtil;

@SpringBootApplication
@EntityScan
@MapperScan("xc.crabapple.standardms.module.*")
public class StandardmsApplication {

    public static void main(String[] args) {
        // 开源版默认从 classpath:application.properties 读取配置；
        // 如沿用企业版 router.conf 机制，将后两个参数替换为 router 路径与 SM4 密钥即可。
        SpringbootUtil.start("Crabapple Standard MS", StandardmsApplication.class, MM.class, args, "", "", 0, 20);
        //
        AppLogUtil.init();
        AppCacheUtil.init();
        AppSecurityUtil.init();
        AppSmsUtil.init();
        AppEnvUtil.init();
        AppScheduleUtil.init();
        ContextUtil.init();
        //
        SpringUtil.banner();
    }

}
