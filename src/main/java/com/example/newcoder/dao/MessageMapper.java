package com.example.newcoder.dao;


import com.example.newcoder.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@SuppressWarnings({"all"})
@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，每个会话只返回最新的一条私信
    List<Message> selectConversations(int userId,int offset,int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个 会话 所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个 会话 所包含的 私信数量.
    int selectLetterCount(String conversationId);

    // 查询 未读私信 的数量，（当前用户的所有未读私信数量、以及每个会话的未读数
    int selectLetterUnreadCount(int userId, String conversationId);
}
