package com.cdk.standardms.common.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.cdk.standardms.common.domain.dao.UserDao;
import tools.jackson.databind.module.SimpleModule;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.filter.CorsFilter;
import xc.crabapple.core.collection.ListUtil;
import xc.crabapple.core.collection.StringUtil;
import xc.crabapple.core.file.properties.PropertiesUtil;
import xc.crabapple.core.json.jackson.JacksonUtil;
import xc.crabapple.core.log.LogUtil;
import xc.crabapple.springboot.SpringbootConfiguration;
import xc.crabapple.ssm.mybatis.MyBatisUtil;
import xc.crabapple.ssm.mybatis.MybatisSqlSessionFactoryBean;
import xc.crabapple.ssm.security.crossdomain.CrossdomainUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xuechong 2025-02-11
 */
@Configuration
public class AppConfiguration extends SpringbootConfiguration {

    /**
     * xuechong 2024-12-28
     * 数据库配置 START
     */
    @Bean(value="mapperScannerConfigurer")
    public MapperScannerConfigurer getMapperScannerConfigurer(){
        final String basePackage = PropertiesUtil.getValue("db.package");
        MapperScannerConfigurer mapperScannerConfigurer = MyBatisUtil.getMapperScannerConfigurer(basePackage);
        return mapperScannerConfigurer;
    }

    /**
     * xuechong 2024-12-28
     * 数据库配置
     */
    @Bean(value="druidDataSource")
    @DependsOn(value="mapperScannerConfigurer")
    public DruidDataSource getDruidDataSource(){
        String dbType = PropertiesUtil.getValue("db.type");
        String url = PropertiesUtil.getValue("db.url");
        String username = PropertiesUtil.getValue("db.username");
        String password = PropertiesUtil.getValue("db.password");
        String xmlUrl = PropertiesUtil.getValue("db.xml.url");
        try {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setUrl(url);
            druidDataSource.setUsername(username);
            druidDataSource.setPassword(password);
            MyBatisUtil.init(druidDataSource, dbType, xmlUrl);
            // 测试连接数据库
            final String dbUrl = MyBatisUtil.testConnection();
            if(!StringUtil.isBlank(dbUrl)){
                // 数据库表字段级别加解密控制
                Map<Class, List<String>> classMap = new HashMap<>();
                classMap.put(UserDao.class, ListUtil.getList("account", "mobile"));
                classMap.put(List.class, ListUtil.getList("account_", "mobile_"));
                MyBatisUtil.setCipher("no", "", "", "", 7, classMap);
            }
            //
            return druidDataSource;
        } catch (Throwable throwable) {
            LogUtil.error("AppConfiguration_getDruidDataSource_t1", throwable, "数据库连接失败，请检查 db.url / db.username / db.password");
            return null;
        }
    }

    /**
     * xuechong 2024-12-28
     * 数据库配置
     */
    @Bean(value="jdbcTemplate")
    @DependsOn(value="druidDataSource")
    public JdbcTemplate getJdbcTemplate(){
        return MyBatisUtil.jdbcTemplate;
    }

    /**
     * xuechong 2024-12-28
     * 数据库配置
     * 构建声明式事务管理器
     */
    @Bean(value="dataSourceTransactionManager")
    @DependsOn(value="druidDataSource")
    public DataSourceTransactionManager getDataSourceTransactionManager() {
        return MyBatisUtil.dataSourceTransactionManager;
    }

    /**
     * xuechong 2024-12-28
     * 数据库配置
     */
    @Bean(value="namedParameterJdbcTemplate")
    @DependsOn(value="druidDataSource")
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(){
        return MyBatisUtil.namedParameterJdbcTemplate;
    }

    /**
     * xuechong 2024-12-28
     * 数据库配置
     */
    @Bean(value="sqlSessionFactoryBean")
    @DependsOn(value="druidDataSource")
    public MybatisSqlSessionFactoryBean getAppSqlSessionFactoryBean(){
        return MyBatisUtil.mybatisSqlSessionFactoryBean;
    }

    /**
     * xuechong 2024-12-28
     * 数据库配置
     */
    @Bean(value="sqlSessionTemplate")
    @DependsOn(value="druidDataSource")
    public SqlSessionTemplate getSqlSessionTemplate(){
        return MyBatisUtil.sqlSessionTemplate;
    }

    /**
     * xuechong 2024-12-28
     * 数据库配置 END
     */
    @Bean(value="beanNameAutoProxyCreator")
    @DependsOn(value="druidDataSource")
    public BeanNameAutoProxyCreator getBeanNameAutoProxyCreator(){
        return MyBatisUtil.beanNameAutoProxyCreator;
    }

    /**
     * xuechong 2024-12-28
     * 创建并配置CORS过滤器
     */
    @Bean(value="corsFilter")
    public CorsFilter getCorsFilter() {
        CorsFilter corsFilter = CrossdomainUtil.getCorsFilter();
        return corsFilter;
    }

    /**
     * xuechong 2024-12-28
     * 方法1：服务器响应给客户端的数据Long类型转为字符串类型，避免客户端接收与处理长整型时精度丢失的问题。
     */
    @Bean(value="simpleModule")
    public SimpleModule getSimpleModule() {
        SimpleModule simpleModule = JacksonUtil.getSimpleModule();
        return simpleModule;
    }

}