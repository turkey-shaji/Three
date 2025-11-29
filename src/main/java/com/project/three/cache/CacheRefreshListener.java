package com.project.three.cache;

import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class CacheRefreshListener implements MessageListener {

    @Autowired
    private Cache<String, String> localCache;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody());
        localCache.invalidate(key); // 删除本地缓存
        System.out.println("刷新本地缓存，删除key: " + key);
    }
}