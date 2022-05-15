package com.example.newcoder.service;

import com.example.newcoder.dao.UserMapper;
import com.example.newcoder.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
