package com.example.newcoder;

import com.example.newcoder.dao.DiscussPostMapper;
import com.example.newcoder.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class testDiscussPost {
    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testDemo01(){
        int i = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(i);

        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPostByuserId(0,0,10);
        for(DiscussPost dp:discussPosts){
            System.out.println(dp);
        }
    }
}
