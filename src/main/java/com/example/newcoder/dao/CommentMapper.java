package com.example.newcoder.dao;

import com.example.newcoder.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@SuppressWarnings({"all"})
@Mapper
public interface CommentMapper {

    // 根据实体，查询分页的评论数据
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);
    // 根据实体，查询评论的数量，用于分页
    int selectCommentsCountByEntity(int entityType,int entityId);

    // 插入评论数据
    int insertComment(Comment comment);

    //
    Comment selectCommentById(int id);

}
