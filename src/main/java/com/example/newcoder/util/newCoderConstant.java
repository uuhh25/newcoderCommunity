package com.example.newcoder.util;

public interface newCoderConstant {

    /**
    * 激活成功
    * */
    int ACTIVATION_SUCCESS=0;
    /**
     * 重复激活
     * */
    int ACTIVATION_REPEAT=1;
    /**
     * 激活失败
     * */
    int ACTIVATION_FAILURE=2;

    /**
     * 默认登录凭证过期时间
     * */
    int DEFAULT_EXPIRED_SECONDS=3600*12;

    /**
     * remember me登录凭证过期时间
     * */
    int REMEMBER_EXPIRED_SECONDS=3600*24*10;

    /**
     * 实体类型：帖子
    * */
    int ENTITY_POST=1;
    /**
     * 实体类型：ping评论
    * */
    int ENTITY_COMMENT=2;
    /**
     * 实体类型：用户
     * */
    int ENTITY_USER=3;

}
