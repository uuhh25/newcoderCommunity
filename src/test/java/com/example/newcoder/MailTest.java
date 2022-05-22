package com.example.newcoder;

import com.example.newcoder.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTest {
    @Autowired
    MailClient mailClient;
    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void test01(){
        boolean flag=mailClient.sendMail("582552546@qq.com","Try","Welcome");
        if (flag) {
            System.out.println("成功");
        }else {
            System.out.println("失败");
        }
    }

    @Test
    public void test02html(){
        Context context=new Context();
        context.setVariable("username","sunday");
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("582552546@qq.com","html",content);
    }
}
