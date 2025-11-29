package com.project.three.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.three.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j // 添加日志注解
public class RedisCache {
    // 缓存类型标识
    private static final String CACHE_TYPE = "Redis";
    // 用户缓存前缀
    private static final String USER_CACHE_PREFIX = "user:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ObjectMapper objectMapper; // 注入Jackson用于JSON序列化

    private ValueOperations<String, Object> valueOps;

    @PostConstruct
    public void initValueOps() {
        this.valueOps = redisTemplate.opsForValue();
    }

    /**
     * 存入用户缓存（JSON序列化）
     */
    public void setUserCache(Long userId, User user) {
        if (userId == null || user == null) {
            log.warn("[{}缓存] 用户缓存存入失败：参数为空", CACHE_TYPE);
            return;
        }
        String key = USER_CACHE_PREFIX + userId;
        try {
            String json = objectMapper.writeValueAsString(user);
            valueOps.set(key, json, 1, TimeUnit.HOURS); // 1小时过期
            log.debug("[{}缓存] 用户{}存入成功", CACHE_TYPE, userId);
        } catch (Exception e) {
            log.error("[{}缓存] 用户{}存入失败", CACHE_TYPE, userId, e);
        }
    }

    /**
     * 获取用户缓存（JSON反序列化）
     */
    public User getUserCache(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = USER_CACHE_PREFIX + userId;
        try {
            String json = (String) valueOps.get(key);
            if (json == null) {
                log.debug("[{}缓存] 用户{}未命中", CACHE_TYPE, userId);
                return null;
            }
            User user = objectMapper.readValue(json, User.class);
            log.debug("[{}缓存] 用户{}读取成功", CACHE_TYPE, userId);
            return user;
        } catch (Exception e) {
            log.error("[{}缓存] 用户{}读取失败", CACHE_TYPE, userId, e);
            return null;
        }
    }

    /**
     * 删除用户缓存
     */
    public void deleteUserCache(Long userId) {
        if (userId == null) {
            return;
        }
        String key = USER_CACHE_PREFIX + userId;
        try {
            redisTemplate.delete(key);
            log.debug("[{}缓存] 用户{}删除成功", CACHE_TYPE, userId);
        } catch (Exception e) {
            log.error("[{}缓存] 用户{}删除失败", CACHE_TYPE, userId, e);
        }
    }

    // 保留基础的get/put方法（通用缓存）
    public String get(String key) {
        try {
            Object value = valueOps.get(key);
            if (value == null) {
                log.debug("[{}缓存] 键{}未命中", CACHE_TYPE, key);
                return null;
            }
            return value.toString();
        } catch (Exception e) {
            log.error("[{}缓存] 键{}读取失败", CACHE_TYPE, key, e);
            return null;
        }
    }

    public void put(String key, String value) {
        try {
            valueOps.set(key, value, 1, TimeUnit.HOURS);
            log.debug("[{}缓存] 键{}存入成功", CACHE_TYPE, key);
        } catch (Exception e) {
            log.error("[{}缓存] 键{}存入失败", CACHE_TYPE, key, e);
        }
    }
}