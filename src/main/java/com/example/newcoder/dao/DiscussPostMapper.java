package com.example.newcoder.dao;


import com.example.newcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@SuppressWarnings({"all"})
@Repository
public interface DiscussPostMapper {
    // 帖子的mapper
    // 根据id进行查询

    List<DiscussPost> selectDiscussPostByuserId(int userId, int offset, int limit);

    // 计算表中总数
    // @Param 注解用于参数别名；如果只有一个参数，并且是动态查询，则要使用别名
    int selectDiscussPostRows(@Param("userId") int userId);

    // 查询帖子详情，通过id查询
    DiscussPost selectDicussPostById(int id);

    // 增加帖子
    int insertDiscussPost(DiscussPost discussPost);

    // 根据id更新帖子的评论数量
    int updatePostCommentCount(int id,int commentCount);

}
