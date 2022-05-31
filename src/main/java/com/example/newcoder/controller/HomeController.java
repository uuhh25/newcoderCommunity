package com.example.newcoder.controller;

import com.example.newcoder.entity.DiscussPost;
import com.example.newcoder.entity.Page;
import com.example.newcoder.entity.User;
import com.example.newcoder.service.DiscussPostService;
import com.example.newcoder.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class HomeController {
    // 根据service 业务层，对视图层进行处理
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    // 首页访问，并且显示首页的帖子
    @RequestMapping(path = {"","/index"},method = RequestMethod.GET)
    public String getIndex(Model model, Page page){
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.countDiscussPost(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                if (user==null) {
                    continue;
                }
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    @RequestMapping(value = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

    @RequestMapping("/log")
    public String log(){
        String name="inke";
        int age=20;

        // 日志从低到高
        log.trace("日志输出 trace");
        log.debug("日志输出 debug");
        log.info("日志输出 info");
        log.warn("日志输出 warn");
        log.error("日志输出 error");

        log.info("name:"+name+",age"+age);
        log.info("name:{},age:{}",name,age);
        // 可以使用{} 占位符来拼接字符串，而不需要使用““+””来连接字符串。

        return "/index";
    }
}
