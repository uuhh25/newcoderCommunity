package com.example.newcoder;

import com.example.newcoder.dao.LoginTicketMapper;
import com.example.newcoder.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class LoginTicketTest {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void test01(){
        final LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("213");
        loginTicket.setId(0);
        loginTicket.setStatus(1);
        loginTicket.setUserId(10);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        final int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);
    }

    @Test
    public void test02(){
        final LoginTicket loginTicket = loginTicketMapper.selectByTicket("000");
        System.out.println(loginTicket);

        // final int i = loginTicketMapper.updateStatus("213", 1);
        // System.out.println(i);
    }

}
