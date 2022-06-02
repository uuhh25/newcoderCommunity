package com.example.newcoder.controller;


import com.example.newcoder.dao.CommentMapper;
import com.example.newcoder.dao.DiscussPostMapper;
import com.example.newcoder.entity.Comment;
import com.example.newcoder.entity.DiscussPost;
import com.example.newcoder.entity.Page;
import com.example.newcoder.entity.User;
import com.example.newcoder.service.CommentService;
import com.example.newcoder.service.DiscussPostService;
import com.example.newcoder.service.LikeService;
import com.example.newcoder.service.UserService;
import com.example.newcoder.util.HostHolder;
import com.example.newcoder.util.newCoderConstant;
import com.example.newcoder.util.newCoderUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@SuppressWarnings({"all"})
@Component
@RequestMapping("/discuss")
public class DiscussPostController implements newCoderConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;  // 线程存储

    @Autowired
    private LikeService likeService;

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

    // 处理查询帖子详情的请求
    @RequestMapping(value = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // 先获得帖子
        DiscussPost post = discussPostService.selectDiscusspost(discussPostId);
        model.addAttribute("post",post);
        // 根据userId获得用户的信息，或者在sql语句中联合查询
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("poster",user);
        model.addAttribute("username",user.getUsername());

        long likeCount = likeService.findEntityLikeCount(ENTITY_POST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        int likeStatus = hostHolder.getUser()==null? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_POST, discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        // 补充评论的信息
            // 评论的分页信息
        page.setLimit(5);
        page.setPath("/dicuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表 (评论下的评论列表)
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        // 即commentVoList中的一个map，存有 所有的评论信息，以及每一个评论下的评论数据和信息
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                //
                likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT, comment.getId());
                likeStatus = hostHolder.getUser()==null? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_COMMENT, comment.getId());
                // 评论的点赞数量和状态
                commentVo.put("likeCount", likeCount);
                commentVo.put("likeStatus", likeStatus);
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表，把offset 和 limit改一下，不分页查询了
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        likeCount = likeService.findEntityLikeCount(ENTITY_COMMENT, reply.getId());
                        likeStatus = hostHolder.getUser()==null? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_COMMENT, reply.getId());
                        // 回复的点赞数量
                        replyVo.put("likeCount", likeCount);
                        replyVo.put("likeStatus", likeStatus);
                        // 对该评论的回复
                        replyVo.put("reply", reply);
                        // 回复的用户
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                // 评论点赞数量

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

}
