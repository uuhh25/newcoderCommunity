package com.example.newcoder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings({"all"})
// 表示自定义注解的使用位置，在方法上
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // 有效的时候：方法运行时
public @interface LoginRequired {



}
