package com.project.three;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ThreeApplication.class) // 指定Spring Boot启动类
public class ConnectionTest {

    // 注入MySQL数据源
    @Autowired
    private DataSource dataSource;

    // 注入Redis模板
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试MySQL连接
     */
    @Test
    public void testMysqlConnection() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("============ MySQL连接测试 ============");
            System.out.println("连接状态：成功");
            System.out.println("数据库URL：" + connection.getMetaData().getURL());
            System.out.println("数据库用户名：" + connection.getMetaData().getUserName());
            System.out.println("数据库驱动：" + connection.getMetaData().getDriverName());
        } catch (SQLException e) {
            System.err.println("============ MySQL连接测试 ============");
            System.err.println("连接状态：失败");
            System.err.println("错误信息：" + e.getMessage());
        }
    }

    /**
     * 测试Redis连接
     */
    @Test
    public void testRedisConnection() {
        try {
            System.out.println("\n============ Redis连接测试 ============");
            // 测试Redis的set/get操作
            stringRedisTemplate.opsForValue().set("test_key", "test_value");
            String value = stringRedisTemplate.opsForValue().get("test_key");

            System.out.println("连接状态：成功");
            System.out.println("Redis测试key：test_key");
            System.out.println("Redis测试value：" + value);

            // 可选：删除测试数据
            stringRedisTemplate.delete("test_key");
        } catch (Exception e) {
            System.err.println("\n============ Redis连接测试 ============");
            System.err.println("连接状态：失败");
            System.err.println("错误信息：" + e.getMessage());
        }
    }

    /**
     * 一次性测试MySQL和Redis连接
     */
    @Test
    public void testAllConnections() {
        testMysqlConnection();
        testRedisConnection();
    }
}