package com.cdk.standardms.common.utils;

import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.file.properties.PropertiesUtil;
import xc.crabapple.ssm.env.EnvUtil;

/**
 * xuechong 2024-07-16
 * 运行环境类
 */
public class AppEnvUtil {

    /**
     * xuechong 2023-10-31 当前项目的运行环境
     */
    public static final void init() {
        String env = PropertiesUtil.getValue("server.env.name");
        if (StringUtil.isBlank(env)) {
            env = "dev";
        }
        EnvUtil.init("", env);
    }

}