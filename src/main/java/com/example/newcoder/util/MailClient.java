package com.example.newcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@SuppressWarnings({"all"})
@Component
public class MailClient {
    //
    private static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    // 起始邮箱
    @Value("${spring.mail.username}")
    private String from;
    // 使用JavaMailSender存储邮件信息？
    // 目的、主题、内容
    public boolean sendMail(String to,String subject,String context){
        //
        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"utf-8");
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(context,true);    // 第二个参数表示可以发送html内容
            // mad 少了一句这个...
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            // 自定义异常，存到logger中
            logger.error("邮件发送失败："+ e.getMessage());
            return false;
        }
        return true;
    }
}
