package com.example.newcoder.service;

import com.example.newcoder.dao.DiscussPostMapper;
import com.example.newcoder.entity.DiscussPost;
import com.example.newcoder.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@SuppressWarnings({"all"})
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPostByuserId(0,0,10);
    }

    public int countDiscussPost(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    // 增加帖子
    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost==null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // title 和 content需要处理
        // 1. html的标签也需要过滤掉,转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // 2. 敏感词过滤
        String stitle=sensitiveFilter.filter(discussPost.getTitle());
        String scontent=sensitiveFilter.filter(discussPost.getContent());

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    // 根据id查询帖子详情
    public DiscussPost selectDiscusspost(int id){
        return discussPostMapper.selectDicussPostById(id);
    }

    // 更新帖子的评论数量
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updatePostCommentCount(id,commentCount);
    }

}
