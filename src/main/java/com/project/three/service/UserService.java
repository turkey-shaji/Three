package com.project.three.service;

import com.project.three.cache.LocalCache;
import com.project.three.cache.RedisCache;
import com.project.three.entity.User;
import com.project.three.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LocalCache localCache;

    @Autowired
    private RedisCache redisCache;

    // 本地缓存key前缀
    private static final String LOCAL_USER_KEY_PREFIX = "user:";

    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            log.warn("无效用户ID：{}", id);
            return null;
        }
        String localKey = LOCAL_USER_KEY_PREFIX + id;

        // 1. 查本地Guava缓存
        User localUser = (User) localCache.get(localKey);
        if (localUser != null) {
            log.info("用户{}：本地Guava缓存命中", id);
            return localUser;
        }

        // 2. 查Redis缓存
        User redisUser = redisCache.getUserCache(id);
        if (redisUser != null) {
            log.info("用户{}：Redis缓存命中，同步到本地Guava缓存", id);
            localCache.put(localKey, redisUser);
            return redisUser;
        }

        // 3. 查数据库
        User dbUser = userMapper.selectById(id);
        if (dbUser != null) {
            log.info("用户{}：数据库查询成功，存入Redis+本地Guava缓存", id);
            redisCache.setUserCache(id, dbUser);
            localCache.put(localKey, dbUser);
        } else {
            log.warn("用户{}：不存在", id);
        }
        return dbUser;
    }

    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            log.warn("更新用户失败：参数无效");
            return false;
        }
        String localKey = LOCAL_USER_KEY_PREFIX + user.getId();
        try {
            int rows = userMapper.updateUser(user);
            if (rows > 0) {
                // 删除双缓存
                localCache.delete(localKey);
                redisCache.deleteUserCache(user.getId());
                log.info("用户{}：更新成功，已删除本地Guava+Redis缓存", user.getId());
                return true;
            } else {
                log.warn("用户{}：不存在，更新失败", user.getId());
                return false;
            }
        } catch (Exception e) {
            log.error("用户{}：更新异常", user.getId(), e);
            return false;
        }
    }
}