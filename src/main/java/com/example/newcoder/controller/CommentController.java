package com.example.newcoder.controller;

import com.example.newcoder.entity.Comment;
import com.example.newcoder.entity.DiscussPost;
import com.example.newcoder.entity.Event;
import com.example.newcoder.event.EventProducer;
import com.example.newcoder.service.CommentService;
import com.example.newcoder.service.DiscussPostService;
import com.example.newcoder.util.CommunityConstant;
import com.example.newcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;  // 用于获取登录用户的信息

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    // 添加的时候，要把对应帖子的id传过来
    @RequestMapping(value = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment)  {
        // 1. 帖子的回帖 2.评论用户 3.回复某个用户

        // 补充comment的信息
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        //
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        // 判断实体类型，找到相应的实体作者，set到Event中
        if (comment.getEntityType() == ENTITY_POST) {
            DiscussPost target = discussPostService.selectDiscusspost(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);  // 发布消息

        // 相当于：评论完之后，重新刷新当前的帖子
        return "redirect:/discuss/detail/" + discussPostId;
    }

}
