package com.example.newcoder;

import com.example.newcoder.dao.UserMapper;
import com.example.newcoder.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class testUser {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testUser01(){
        final User user = userMapper.selectById(1);
        System.out.println(user);
    }
}
