package com.cdk.standardms.common.configuration;

import com.cdk.standardms.module.index.IndexServiceImpl;
import com.cdk.standardms.module.login.LoginServiceImpl;
import com.cdk.standardms.module.properties.PropertiesMapper;
import com.cdk.standardms.module.properties.PropertiesServiceImpl;
import com.cdk.standardms.module.sms.SmsMapper;
import com.cdk.standardms.module.sms.SmsServiceImpl;
import com.cdk.standardms.module.systemlog.SystemlogMapper;
import com.cdk.standardms.module.systemlog.SystemlogServiceImpl;
import com.cdk.standardms.module.user.UserMapper;
import com.cdk.standardms.module.user.UserServiceImpl;

/**
 * xuechong 2022-04-07
 * 模块管理器
 * 在项目的spring容器初始化之后，执行代码：ModuleManager.injection(MM.class); 本类的全局静态变量Service与Mapper才能自动被注入（实例化）。
 */
public class MM {

    public static IndexServiceImpl indexService;

    public static PropertiesServiceImpl propertiesService;
    public static PropertiesMapper propertiesMapper;

    public static UserServiceImpl userService;
    public static UserMapper userMapper;

    public static LoginServiceImpl loginService;

    public static SmsServiceImpl smsService;
    public static SmsMapper smsMapper;

    public static SystemlogServiceImpl systemlogService;
    public static SystemlogMapper systemlogMapper;

}