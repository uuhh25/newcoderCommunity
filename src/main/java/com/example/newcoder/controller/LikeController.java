package com.example.newcoder.controller;


import com.example.newcoder.entity.User;
import com.example.newcoder.service.LikeService;
import com.example.newcoder.util.HostHolder;
import com.example.newcoder.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"all"})
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId){
        // 获取当前的点赞用户
        User user = hostHolder.getUser();
        // 实现点赞
        likeService.like(user.getId(),entityType,entityId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // 因为这是异步请求，所以返回的是json格式数据
        String jsonString = newCoderUtil.getJSONString(0, null, map);
        return jsonString;

    }

}
