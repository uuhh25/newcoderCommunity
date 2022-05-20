package com.example.newcoder.config;

import com.example.newcoder.annotation.LoginRequired;
import com.example.newcoder.controller.interceptor.AlphaInterceptor;
import com.example.newcoder.controller.interceptor.LoginRequiredInterceptor;
import com.example.newcoder.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SuppressWarnings({"all"})
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    /**
     拦截器的配置是在webmvc下进行配置的
     * */
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg")    // 直接不拦截的路径
                .addPathPatterns("/index","/register","/login");  // 具体要拦截的路径

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg")    // 直接不拦截的路径
                ;  // 具体要拦截的路径

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg")    // 直接不拦截的路径
        ;  // 具体要拦截的路径
    }
}
