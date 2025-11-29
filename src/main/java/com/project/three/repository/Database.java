package com.project.three.repository;

import com.project.three.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jakarta.annotation.Resource;
import java.util.List;

@Repository
public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据ID查询用户（返回User对象，适配业务层需求）
     */
    public User getUserById(Long userId) {
        try {
            String sql = "SELECT id, username, email, create_time FROM user WHERE id = ?";
            // 使用query而非queryForObject，避免查询不到数据抛异常
            List<User> userList = jdbcTemplate.query(
                    sql,
                    new Object[]{userId},
                    new BeanPropertyRowMapper<>(User.class)
            );
            // 存在数据返回第一个，否则返回null
            return userList.isEmpty() ? null : userList.get(0);
        } catch (Exception e) {
            log.error("查询用户失败（ID：{}）", userId, e);
            return null;
        }
    }

    /**
     * 保留原String返回的方法（若有其他场景使用）
     */
    public String getData(String userId) {
        try {
            Long id = Long.parseLong(userId);
            User user = getUserById(id);
            return user != null ? user.toString() : "用户不存在";
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误：{}", userId, e);
            return "无效的用户ID";
        } catch (Exception e) {
            log.error("查询数据库失败", e);
            return "查询异常";
        }
    }
}