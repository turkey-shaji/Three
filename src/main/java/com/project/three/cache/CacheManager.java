package com.project.three.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.three.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_CACHE_PREFIX = "user:";

    public void setUserCache(Long userId, User user) {
        try {
            String key = USER_CACHE_PREFIX + userId;
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(user));
        } catch (Exception e) {
            log.error("用户{}缓存更新失败", userId, e);
        }
    }

    public User getUserCache(Long userId) {
        try {
            String key = USER_CACHE_PREFIX + userId;
            String json = redisTemplate.opsForValue().get(key);
            return json == null ? null : objectMapper.readValue(json, User.class);
        } catch (Exception e) {
            log.error("用户{}缓存读取失败", userId, e);
            return null;
        }
    }

    public void deleteUserCache(Long userId) {
        try {
            redisTemplate.delete(USER_CACHE_PREFIX + userId);
        } catch (Exception e) {
            log.error("用户{}缓存删除失败", userId, e);
        }
    }
}