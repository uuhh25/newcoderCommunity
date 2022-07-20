package com.example.newcoder.controller;

import com.example.newcoder.entity.User;
import com.example.newcoder.service.UserService;
import com.example.newcoder.util.RedisKeyUtil;
import com.example.newcoder.util.newCoderConstant;
import com.example.newcoder.util.newCoderUtil;
import com.google.code.kaptcha.Producer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"all"})
@Controller
public class LoginController implements newCoderConstant {

    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("server.servlet.context-path")
    private String contentPath;

    @Autowired
    private RedisTemplate redisTemplate;


    // 拦截注册页面的请求，让请求返回我们解析过的页面
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        // 是否注册成功，并且进行提示
        Map<String, Object> map = userService.register(user);
        if(map==null || map.isEmpty()){
            // 注册成功
            model.addAttribute("msg","注册成功，我们向您发生了一封邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            // 出现问题的情况
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 处理激活的情况，即同url中获取需要的userId 和 Code，然后进行判断
    // http://localhost:8080/community/activation/101/code  path表示路径后的一些参数
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    // 验证码的生成
    @RequestMapping(value = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response ){
        // 要回应所以要response，要生成验证码，所以要session来存验证码以匹配
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入session
        // session.setAttribute("kaptcha",text);
        // 要把验证码存到redis中！首先需要一个随机的ownner id，存入到cookie中
        String kaptchaOwner = newCoderUtil.generateUUID();
        Cookie cookie=new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);   // 过期时间
        cookie.setPath(contentPath);
        response.addCookie(cookie);
        // 将验证码存入 到对应的redis的key中
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey,text, 60,TimeUnit.SECONDS);  // 存入，并且设置过期时间

        // 图片传给浏览器，通过io流
        response.setContentType("img/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }

    // 登录的请求
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String userLogin(Model model,String username,
                            String password,String code,boolean rememberme,
                            HttpServletResponse response,
                            @CookieValue("kaptchaOwner")String kaptchaOwner){
        // 判断登录是否有问题,验证码从session中取 ,登陆凭证的key（ticket）存到cookie中
        // 1.验证码 从redis中获取
        // final String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha=null;
        if (!StringUtils.isEmptyOrWhitespace(kaptchaOwner)){
            final String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha=(String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if (StringUtils.isEmptyOrWhitespace(code)||StringUtils.isEmptyOrWhitespace(kaptcha)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        // 2.账号密码
        // 过期时间
        int seconds;
        if (rememberme) seconds=REMEMBER_EXPIRED_SECONDS;
        else seconds=DEFAULT_EXPIRED_SECONDS;

        final Map<String, Object> map = userService.login(username, password, seconds);
        if (map.containsKey("ticket")){
            // 登录成功则带有ticket，则我们需要把这个ticket存到cookie中
            final Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contentPath);    // 用于干嘛的？  是用于设置有效路径（范围
            cookie.setMaxAge(seconds);
            // cookie发送到页面
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMSg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    // 退出
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket){
        // 通过cookie获取ticket
        userService.logout(ticket);
        return "redirect:/login";
    }

}
