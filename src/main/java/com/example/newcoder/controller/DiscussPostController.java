package com.example.newcoder.controller;

import com.example.newcoder.dao.DiscussPostMapper;
import com.example.newcoder.entity.DiscussPost;
import com.example.newcoder.entity.User;
import com.example.newcoder.service.DiscussPostService;
import com.example.newcoder.util.HostHolder;
import com.example.newcoder.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@SuppressWarnings({"all"})
@Component
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;  // 线程存储

    //  客户端发送请求
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String addDiscusPost(String title,String content){
        // 登陆了才能够有发帖的功能 => 判断是否登录
        User user=hostHolder.getUser();
        if (user==null){
            return newCoderUtil.getJSONString(403,"还没有登录");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况,将来统一处理.
        return newCoderUtil.getJSONString(0, "发布成功!");

    }

}
