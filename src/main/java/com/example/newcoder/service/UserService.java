package com.example.newcoder.service;

import com.example.newcoder.dao.LoginTicketMapper;
import com.example.newcoder.dao.UserMapper;
import com.example.newcoder.entity.LoginTicket;
import com.example.newcoder.entity.User;
import com.example.newcoder.util.MailClient;
import com.example.newcoder.util.newCoderConstant;
import com.example.newcoder.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings({"all"})
@Service
public class UserService implements newCoderConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    // 用user保存参数
    public Map<String,Object> register(User user){

        // 用map装载信息，传给视图
        Map<String,Object>map=new HashMap<>();

        // user信息的判断
        if (user==null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isEmptyOrWhitespace(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isEmptyOrWhitespace(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isEmptyOrWhitespace(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 信息的验证, 是否存在
        User u=userMapper.selectByName(user.getUsername()); //根据用户名查询
        if (u!=null){
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册, user信息的传递
        user.setSalt(newCoderUtil.generateUUID().substring(0,5));
        user.setPassword(newCoderUtil.md5(user.getPassword()+user.getSalt()));  // 对密码进行md5加密和加盐加密
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(newCoderUtil.generateUUID());    //激活码
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);    //注册

        // 激活邮件,通过发送html文件
        final Context context = new Context();
        context.setVariable("email",user.getEmail());
        // 一般的激活url为,http://localhost:8080/path/activation/id(101)/acti_code
        String url=domain+contextPath+"activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"activation",content);
        return map;
    }

    // 根据userId和激活码code，判断是否激活成功
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    // 用户的登录操作
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        // password还需要md5+salt
        Map<String,Object> map=new HashMap<>();

        // 空值的处理
        if (StringUtils.isEmptyOrWhitespace(username)){
            map.put("usernameMsg","账户不能为空");
            return map;
        }
        if (StringUtils.isEmptyOrWhitespace(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        // 账号的状态验证
        final User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        if (user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        // 密码验证
        final String s = newCoderUtil.md5(password + user.getSalt());
        if(!s.equals(user.getPassword())){
            map.put("passwordMsg","密码错误");
            return map;
        }

        // 是否已有登录凭证且未过期
        //final LoginTicket lTicket = loginTicketMapper.selectByUserId(user.getId());
        //if (lTicket!=null && lTicket.getStatus()==0 && lTicket.getExpired().after(new Date())){
        //    map.put("ticket",lTicket.getTicket());
        //    return map;
        //}

        // 如果都没错，则生成登录凭证
        final LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(newCoderUtil.generateUUID()); // 随机,用于验证是否有有效的登陆凭证
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*expiredSeconds));
        final int i = loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    // 退出
    public void logout(String ticket){
        //
        loginTicketMapper.updateStatus(ticket,1);
    }

    // 根据ticket查询的用户
    public LoginTicket findTicket(String ticket){
        final LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        return loginTicket;
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }
}
