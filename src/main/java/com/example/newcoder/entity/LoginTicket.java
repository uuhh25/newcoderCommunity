package com.example.newcoder.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoginTicket {
    private int id;
    private int user_id;
    private String ticket;
    private int status;
    private Date expired;
}
