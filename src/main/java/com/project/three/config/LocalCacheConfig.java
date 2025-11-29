package com.project.three.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class LocalCacheConfig {
    @Bean("guavaLocalCache") // 重命名Bean
    public Cache<String, String> localCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(10000) // 最大缓存条目
                .expireAfterWrite(5, TimeUnit.MINUTES) // 写入后5分钟过期
                .concurrencyLevel(8) // 并发级别
                .recordStats() // 开启统计
                .build();
    }
}