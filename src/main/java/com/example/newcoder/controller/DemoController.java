package com.example.newcoder.controller;

import com.example.newcoder.util.newCoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings({"all"})
@Controller
public class DemoController {

    // cookie示例

    // 创建cookie
    @RequestMapping(value = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookier(HttpServletResponse response){
        // 创建
        Cookie cookie = new Cookie("demo01", newCoderUtil.generateUUID());
        // 设置cookie生效的范围
        cookie.setPath("/"); //
        cookie.setMaxAge(10*1);
        // 发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(value = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookier(@CookieValue("demo01") String code){
        //
        System.out.println(code);
        return "get cookie";
    }

    // session 示例
    @RequestMapping(value = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        // 创建
        session.setAttribute("id","1");
        session.setAttribute("name","demo01");
        return "set session";
    }

    @RequestMapping(value = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        // 创建
        final Object id = session.getAttribute("id");
        final Object name = session.getAttribute("name");
        System.out.println((String) id + (String) name);
        return "get session";
    }

}
