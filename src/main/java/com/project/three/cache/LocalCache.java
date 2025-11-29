package com.project.three.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j // 添加日志注解
public class LocalCache {
    // 缓存类型标识
    private static final String CACHE_TYPE = "本地Guava";
    private final Cache<String, Object> cache;

    public LocalCache() {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    /**
     * 获取本地缓存（带日志）
     */
    public Object get(String key) {
        try {
            Object value = cache.getIfPresent(key);
            if (value == null) {
                log.debug("[{}缓存] 键{}未命中", CACHE_TYPE, key);
            } else {
                log.debug("[{}缓存] 键{}读取成功", CACHE_TYPE, key);
            }
            return value;
        } catch (Exception e) {
            log.error("[{}缓存] 键{}读取异常", CACHE_TYPE, key, e);
            return null;
        }
    }

    /**
     * 存入本地缓存（带日志）
     */
    public void put(String key, Object value) {
        try {
            cache.put(key, value);
            log.debug("[{}缓存] 键{}存入成功", CACHE_TYPE, key);
        } catch (Exception e) {
            log.error("[{}缓存] 键{}存入异常", CACHE_TYPE, key, e);
        }
    }

    /**
     * 删除本地缓存（带日志）
     */
    public void delete(String key) {
        try {
            cache.invalidate(key);
            log.debug("[{}缓存] 键{}删除成功", CACHE_TYPE, key);
        } catch (Exception e) {
            log.error("[{}缓存] 键{}删除异常", CACHE_TYPE, key, e);
        }
    }
}