package com.example.newcoder.aspect;

import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.example.newcoder.service.*.*(..))")
    public void pointcut(){

    }

    //    @Before("pointcut()")
    //    public void before(JoinPoint joinPoint){
    //        //用户的ip ，在 xxx时间，访问了xx什么功能[com......service.xxxx()]
    //        // 利用RequestContextHolder 获取 Request 去获取ip
    //        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    //        HttpServletRequest request = requestAttributes.getRequest();
    //        String remoteHost = request.getRemoteHost();
    //        String now=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    //        String taretService=joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
    //        logger.info(String.format("用户[%s],在[%s]，访问了[%s].",remoteHost,now,taretService));
    //        //
    //    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户[1.2.3.4],在[xxx],访问了[com.nowcoder.community.service.xxx()].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes==null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
    }
}
