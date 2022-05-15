package com.example.newcoder.entity;


import lombok.Data;

import java.util.Date;

@Data
// getter 和 setter 还有有参构造器 + 无参构造器
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private int commentCount;
    private double score;
    private Date createTime;

}
