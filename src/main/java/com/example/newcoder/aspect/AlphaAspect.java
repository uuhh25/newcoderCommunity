package com.example.newcoder.aspect;


import ch.qos.logback.classic.spi.ThrowableProxy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@SuppressWarnings({"all"})
@Component  //变成Bean组件
@Aspect
public class AlphaAspect {

    // 设置目标bean,即项目service目录下的所有类的方法
    @Pointcut("execution(* com.example.newcoder.service.*.*(..))")
    public void pointcut(){

    }

    // 定义advice；连接点的前、后、返回、异常
    //    @Before("pointcut()")
    //    public void before(){
    //        System.out.println("before");
    //    }
    //    @After("pointcut()")
    //    public void after(){
    //        System.out.println("after");
    //    }
    //    @AfterReturning("pointcut()")
    //    public void afterReturning(){
    //        System.out.println("after");
    //    }
    //    @AfterThrowing("pointcut()")
    //    public void afterThrowing(){
    //        System.out.println("after");
    //    }
    //    @Around("pointcut()")   // 可以同时定义前、后的操作
    //    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
    //        // 调用目标组件之前...
    //        System.out.println("调用前");
    //        Object proceed = joinPoint.proceed();//调用目标组件的方法
    //
    //        // 调用目标组件之后...
    //        System.out.println("调用后");
    //        return proceed;
    //    }

}
