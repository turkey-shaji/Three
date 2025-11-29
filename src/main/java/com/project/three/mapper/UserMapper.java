package com.project.three.mapper;

import com.project.three.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

// UserMapper.java（纯接口，无注解）
@Mapper
public interface UserMapper {
    User selectById(Long id);
    int updateUser(User user);
}