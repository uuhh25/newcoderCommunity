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

    int insertDiscussPost(DiscussPost discussPost);
}
