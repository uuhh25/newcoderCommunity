package com.example.newcoder.service;

import com.example.newcoder.dao.DiscussPostMapper;
import com.example.newcoder.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPostByuserId(0,0,10);
    }

    public int countDiscussPost(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
