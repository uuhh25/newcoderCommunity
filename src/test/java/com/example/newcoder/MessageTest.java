package com.example.newcoder;

import com.example.newcoder.dao.MessageMapper;
import com.example.newcoder.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MessageTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void test01(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 10);
        System.out.println(messages);

        final int i = messageMapper.selectConversationCount(111);
        System.out.println(i);

        final List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages1) {
            System.out.println(message);
        }

        final int i1 = messageMapper.selectLetterCount("111_112");
        System.out.println(i1);

        final int i2 = messageMapper.selectLetterUnreadCount(111, "111_112");
        System.out.println("未读私信数量"+i2);

    }

}
