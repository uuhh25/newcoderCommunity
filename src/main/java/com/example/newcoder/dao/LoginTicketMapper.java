package com.example.newcoder.dao;

import com.example.newcoder.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Deprecated // 不推荐使用的注解
public interface LoginTicketMapper {
    // 登陆凭证的需求： 插入凭证、凭证失效(凭证状态的修改)、通过ticket(存到cookie)查询凭证
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket,int status);

    LoginTicket selectByUserId(int userId);
}
