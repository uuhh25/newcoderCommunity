package com.example.newcoder.service;

import com.example.newcoder.dao.CommentMapper;
import com.example.newcoder.dao.DiscussPostMapper;
import com.example.newcoder.entity.Comment;
import com.example.newcoder.util.SensitiveFilter;
import com.example.newcoder.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@SuppressWarnings({"all"})
@Service
public class CommentService implements CommunityConstant {

    //
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostMapper discussPostMapper;


    // 根据实体查询帖子的评论
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }
    // 评论数量
    public int findCommentCount(int entityType,int entityId){
        return commentMapper.selectCommentsCountByEntity(entityType,entityId);
    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

    // 添加评论,把添加评论的操作放入到一个事务中，保证完成
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        // 要对内容进行过滤
        if (comment==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int i = commentMapper.insertComment(comment);

        // 更新评论的数量，只有更新给帖子的时候，才增加总数量;
        if (comment.getEntityType()==ENTITY_POST){
            int count=commentMapper.selectCommentsCountByEntity(ENTITY_POST,comment.getEntityId());
            // 更新帖子的总评论数量;参数:新的数量，对应的帖子，comment中的entity_id对应的就是实体的id
            discussPostMapper.updatePostCommentCount(comment.getEntityId(),count);
        }

        return i;
    }
}
