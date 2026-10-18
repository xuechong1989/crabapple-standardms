package xc.crabapple.standardms.common.configuration;

import xc.crabapple.standardms.module.index.IndexServiceImpl;
import xc.crabapple.standardms.module.login.LoginServiceImpl;
import xc.crabapple.standardms.module.properties.PropertiesMapper;
import xc.crabapple.standardms.module.properties.PropertiesServiceImpl;
import xc.crabapple.standardms.module.sms.SmsMapper;
import xc.crabapple.standardms.module.sms.SmsServiceImpl;
import xc.crabapple.standardms.module.systemlog.SystemlogMapper;
import xc.crabapple.standardms.module.systemlog.SystemlogServiceImpl;
import xc.crabapple.standardms.module.user.UserMapper;
import xc.crabapple.standardms.module.user.UserServiceImpl;

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