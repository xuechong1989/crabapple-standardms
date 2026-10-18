package com.cdk.standardms.module.sms;

import com.cdk.standardms.common.configuration.MM;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.core.sms.SmsUtil;

/**
 * xuechong 2025-01-15
 * 短信发送工具类，基于腾讯短信平台。
 * 所有凭据均从配置读取，未配置时自动停用，不在源码中保留任何默认密钥。
 */
public final class AppSmsUtil {

    public static final void init(){
        final String enable = MM.propertiesService.getString("sms_tencent_enable", "no");
        if(!"yes".equals(enable)){
            return;
        }
        // 腾讯云短信凭据由使用者自行申请并填写到 application.properties，仓库不提供任何默认值
        final String secretId = MM.propertiesService.getString("sms_tencent_secretid", "");
        final String secretKey = MM.propertiesService.getString("sms_tencent_secretkey", "");
        final String smsSdkAppId = MM.propertiesService.getString("sms_tencent_appid", "");
        final String templateId = MM.propertiesService.getString("sms_tencent_templateid", "");
        final String signName = MM.propertiesService.getString("sms_tencent_sign", "");
        if(StringUtil.isBlank(secretId, secretKey, smsSdkAppId, templateId, signName)){
            LogUtil.error("AppSmsUtil_init_1", "短信通道已启用但配置不完整，已自动停用");
            return;
        }
        final String proxyEnable = MM.propertiesService.getString("sms_tencent_proxy_enable", "no");
        final String protocol = MM.propertiesService.getString("sms_tencent_proxy_protocol", "http://");
        final String proxyIp = MM.propertiesService.getString("sms_tencent_proxy_ip", "");
        final String proxyPort = MM.propertiesService.getString("sms_tencent_proxy_port", "0");
        SmsUtil.initTencent(secretId, secretKey, smsSdkAppId, templateId, signName, proxyEnable, protocol, proxyIp, proxyPort);
    }

}
