package com.cdk.standardms.common.utils;

import com.cdk.standardms.common.configuration.MM;
import xc.crabapple.ssm.security.SecurityConfigBean;
import xc.crabapple.ssm.security.SecurityUtil;

/**
 * xuechong 2025-01-21
 */
public class AppSecurityUtil {

    public static final void init(){
        //
        final SecurityConfigBean securityConfigBean = new SecurityConfigBean();
        //
        final String rootApi = MM.propertiesService.getString("security_root_api", "/api/crabapple/standardms");
        securityConfigBean.setRootApi(rootApi);
        //
        final String ipApiEnable = MM.propertiesService.getString("security_ipapi_enable", "no");
        final String ipApiValue = MM.propertiesService.getString("security_ipapi_value", "");
        securityConfigBean.setIpApiEnable(ipApiEnable);
        securityConfigBean.setIpApiValue(ipApiValue);
        //
        final String apiParamEnable = MM.propertiesService.getString("security_apiparam_enable", "no");
        final String apiParamValue = MM.propertiesService.getString("security_apiparam_value", "");
        securityConfigBean.setApiParamEnable(apiParamEnable);
        securityConfigBean.setApiParamValue(apiParamValue);
        //
        final String crossdomainValue = MM.propertiesService.getString("security_crossdomain_value", "*");
        securityConfigBean.setCrossdomainValue(crossdomainValue);
        //
        final String sameoriginEnable = MM.propertiesService.getString("security_sameorigin_enable", "no");
        final String sameOriginValue = MM.propertiesService.getString("security_sameorigin_value", null);
        securityConfigBean.setSameoriginEnable(sameoriginEnable);
        securityConfigBean.setSameOriginValue(sameOriginValue);
        //
        final String xssEnable = MM.propertiesService.getString("security_xss_enable", "no");
        final String xssApiWhitelist = MM.propertiesService.getString("security_xss_apiwhitelist", "");
        securityConfigBean.setXssEnable(xssEnable);
        securityConfigBean.setXssApiWhitelist(xssApiWhitelist);
        //
        final String csrfEnable = MM.propertiesService.getString("security_csrf_enable", "no");
        final Integer csrfTokenLength = MM.propertiesService.getInt("security_csrf_token_length", 128);
        // 以下密钥默认留空；启用 CSRF / 传参加密时，请自行生成并填入配置
        final String csrfTokenSubmergeKey = MM.propertiesService.getString("security_csrf_token_submerge_key", "");
        final String csrfTokenParamName = MM.propertiesService.getString("security_csrf_token_param_name", "csrf_token");
        final String csrfFirstApi = MM.propertiesService.getString("security_csrf_firstapi", "/api/crabapple/standardms/index/main");
        securityConfigBean.setCsrfEnable(csrfEnable);
        securityConfigBean.setCsrfTokenLength(csrfTokenLength);
        securityConfigBean.setCsrfTokenSubmergeKey(csrfTokenSubmergeKey);
        securityConfigBean.setCsrfTokenParamName(csrfTokenParamName);
        securityConfigBean.setCsrfFirstApi(csrfFirstApi);
        //
        final String limiterEnable = MM.propertiesService.getString("security_limiter_enable", "yes");
        final String limiterClientIdRule = MM.propertiesService.getString("security_limiter_clientid_rule", "*#*#10#20#30");
        final String limiterClientIpRule = MM.propertiesService.getString("security_limiter_clientip_rule", "");
        securityConfigBean.setLimiterEnable(limiterEnable);
        securityConfigBean.setLimiterClientIdRule(limiterClientIdRule);
        securityConfigBean.setLimiterClientIpRule(limiterClientIpRule);
        //
        final String uploaderWebshellEnable = MM.propertiesService.getString("security_uploader_webshell_enable", "yes");
        final String uploaderWebshellParamName = MM.propertiesService.getString("security_uploader_webshell_paramname", "targetScope");
        securityConfigBean.setUploaderWebshellEnable(uploaderWebshellEnable);
        securityConfigBean.setUploaderWebshellParamName(uploaderWebshellParamName);
        //
        final String tcEnbale = MM.propertiesService.getString("security_tc_enbale", "no");
        final String tcC2sSubmergeKey = MM.propertiesService.getString("security_tc_c2s_submergekey", "");
        final String tcC2sSm2publicKey = MM.propertiesService.getString("security_tc_c2s_sm2_publickey", "");
        final String tcC2sSm2privateKey = MM.propertiesService.getString("security_tc_c2s_sm2_privatekey", "");
        final String tcC2sApiWhitelist = MM.propertiesService.getString("security_tc_c2s_apiwhitelist", "");
        final String tcS2cSubmergeKey = MM.propertiesService.getString("security_tc_s2c_submergekey", "");
        final String tcS2cSm2publicKey = MM.propertiesService.getString("security_tc_s2c_sm2_publickey", "");
        final String tcS2cSm2privateKey = MM.propertiesService.getString("security_tc_s2c_sm2_privatekey", "");
        final String tcS2cApiWhitelist = MM.propertiesService.getString("security_tc_s2c_apiwhitelist", "/api/crabapple/standardms/index/heart");
        securityConfigBean.setTcEnbale(tcEnbale);
        securityConfigBean.setTcC2sSubmergeKey(tcC2sSubmergeKey);
        securityConfigBean.setTcC2sSm2publicKey(tcC2sSm2publicKey);
        securityConfigBean.setTcC2sSm2privateKey(tcC2sSm2privateKey);
        securityConfigBean.setTcC2sApiWhitelist(tcC2sApiWhitelist);
        securityConfigBean.setTcS2cSubmergeKey(tcS2cSubmergeKey);
        securityConfigBean.setTcS2cSm2publicKey(tcS2cSm2publicKey);
        securityConfigBean.setTcS2cSm2privateKey(tcS2cSm2privateKey);
        securityConfigBean.setTcS2cApiWhitelist(tcS2cApiWhitelist);
        //
        SecurityUtil.init(securityConfigBean);
    }

}
