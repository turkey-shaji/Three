package com.project.three.controller;

import com.project.three.entity.User;
import com.project.three.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")  // 去掉末尾斜杠，更规范
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据ID查询用户
     * 请求示例：http://localhost:8080/users/1
     */
    @GetMapping("/{id}")  // 添加斜杠，路径更清晰
    public User getUserById(@PathVariable Long id) {
        log.info("查询用户：{}", id);
        User user = userService.getUserById(id);
        return user;
    }

    /**
     * 更新用户
     * 请求示例：PUT http://localhost:8080/users
     */
    @PutMapping
    public String updateUser(@RequestBody User user) {
        log.info("更新用户：{}", user.getId());
        boolean success = userService.updateUser(user);
        return success ? "更新成功" : "更新失败";
    }
}