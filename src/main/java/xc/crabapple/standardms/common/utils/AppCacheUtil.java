package xc.crabapple.standardms.common.utils;

import xc.crabapple.standardms.common.configuration.MM;
import xc.crabapple.ssm.cache.CacheUtil;

/**
 * xuechong 2025-01-14
 * xuechong 2025-01-26 底层封装了CacheUtil类
 * 缓存工具类。
 * 默认关闭 Redis，使用本地缓存；Redis 地址、密码、加密密钥均从配置读取。
 */
public class AppCacheUtil {

    /**
     * xuechong 2025-01-26
     */
    public static final void init(){
        // 本地默认值
        Long localClearInterval = MM.propertiesService.getLong("cache_local_clear_interval", 600L);
        String redisEnable = MM.propertiesService.getString("cache_redis_enable", "no");
        String redisServerId = MM.propertiesService.getString("cache_redis_serverid", "crabapple-standardms");
        String redisModel = MM.propertiesService.getString("cache_redis_model", "singleton");
        String redisHost = MM.propertiesService.getString("cache_redis_host", "127.0.0.1:6379");
        // Redis 地址、口令与 SM4 密钥由使用者自行配置；默认关闭，不提供任何默认密钥
        String redisPassword = MM.propertiesService.getString("cache_redis_password", "");
        Integer redisReconnectMaxCount = MM.propertiesService.getInt("cache_redis_reconnect_maxcount", 3);
        String redisCipherType = MM.propertiesService.getString("cache_redis_cipher_type", "sm4");
        String redisCipherKey = MM.propertiesService.getString("cache_redis_cipher_key", "");
        //
        CacheUtil.init(localClearInterval, redisEnable, redisServerId, redisHost, redisModel, redisPassword, redisReconnectMaxCount, redisCipherType, redisCipherKey);
        //
        if("yes".equals(redisEnable)){
            CacheUtil.testConnection();
        }
    }

}
