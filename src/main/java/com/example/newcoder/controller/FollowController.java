package com.example.newcoder.controller;

import com.example.newcoder.entity.User;
import com.example.newcoder.service.FollowService;
import com.example.newcoder.util.HostHolder;
import com.example.newcoder.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings({"all"})
@Controller
public class FollowController { // 异步操作
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    // 关注
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){  // entityType我们写了一个全局变量
        // 先要从当前用户种获取id
        User user = hostHolder.getUser();
        if (user==null){  //不需要做了，已经有一个loginRequiredInterceptor了
            return "/site/login";
        }
        followService.follow(user.getId(),entityType,entityId);

        return newCoderUtil.getJSONString(0,"关注成功");
    }

    // 取关
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){  // entityType我们写了一个全局变量
        // 先要从当前用户种获取id
        User user = hostHolder.getUser();
        //        if (user==null){  不需要做了，已经有一个loginRequiredInterceptor了
        //            return "/site/login";
        //        }
        followService.unfollow(user.getId(),entityType,entityId);

        return newCoderUtil.getJSONString(0,"取消关注成功");
    }
}
