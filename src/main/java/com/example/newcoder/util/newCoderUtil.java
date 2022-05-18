package com.example.newcoder.util;

import org.springframework.util.DigestUtils;
import org.thymeleaf.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
@SuppressWarnings({"all"})
public class newCoderUtil {

    // 工具类
    // 1.生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    // 2.MD5加密; 只能加密，不能解密; 并且还得对MD5加密的结果进行加盐，以对抗 MD5密码库
    public static String md5(String key){
        if (StringUtils.isEmptyOrWhitespace(key)){
            return "";
        }
        return DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
    }
}
