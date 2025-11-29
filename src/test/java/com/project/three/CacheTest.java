package com.project.three;

import com.project.three.cache.CacheManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testCacheHit() {
        String key = "1"; // 数据库中存在的用户ID

        // 第一次查询：走数据库，同时写入Redis和LocalCache
        String result1 = cacheManager.getData(key);
        System.out.println("第一次查询结果：" + result1);

        // 第二次查询：走LocalCache（一级缓存）
        String result2 = cacheManager.getData(key);
        System.out.println("第二次查询结果：" + result2);

        // 验证两次结果一致
        assert result1.equals(result2);
    }
}
