package com.example.newcoder.controller.interceptor;


import com.example.newcoder.entity.LoginTicket;
import com.example.newcoder.entity.User;
import com.example.newcoder.service.UserService;
import com.example.newcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import static com.example.newcoder.util.cookieUtil.getValue;

@SuppressWarnings({"all"})
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie获取ticket
        String ticket=getValue(request,"ticket");

        if (ticket!=null){
            // 这里本来是用mysql+mapper的，现在改为了redis
            final LoginTicket loginTicket = userService.findTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket!=null && loginTicket.getStatus()!=1 && loginTicket.getExpired().after(new Date())){
                final User loginUser = userService.findUserById(loginTicket.getUserId());
                // 让本次请求持有用户，要考虑多线程的情况;即把用户存到线程中，使得能够一直被获取
                hostHolder.setUsers(loginUser);
            }
        }

        return true;

    }

    // 把登录的用户信息从线程中取出，放入到模板引擎中！！
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在模板引擎解析之前，要使用查询到的用户信息，即我们从线程中获取
        final User user = hostHolder.getUser();
        if (user!=null && modelAndView!=null){
            modelAndView.addObject("user",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在模板执行之后，清空线程的信息
        hostHolder.clear();
    }
}
