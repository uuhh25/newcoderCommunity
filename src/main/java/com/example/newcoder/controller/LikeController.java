package com.example.newcoder.controller;


import com.example.newcoder.entity.Event;
import com.example.newcoder.entity.User;
import com.example.newcoder.event.EventProducer;
import com.example.newcoder.service.LikeService;
import com.example.newcoder.util.CommunityConstant;
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
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
    // 重构了方法，即获取被点赞的实体id？
    public String like(int entityType, int entityId,int entityUserId,int postId){
        // 获取当前的点赞用户
        User user = hostHolder.getUser();
        // 实现点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        // 因为这是异步请求，所以返回的是json格式数据
        String jsonString = newCoderUtil.getJSONString(0, null, map);
        return jsonString;

    }

}
