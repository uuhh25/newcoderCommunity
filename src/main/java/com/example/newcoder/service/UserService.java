package com.example.newcoder.service;

import com.example.newcoder.dao.LoginTicketMapper;
import com.example.newcoder.dao.UserMapper;
import com.example.newcoder.entity.LoginTicket;
import com.example.newcoder.entity.User;
import com.example.newcoder.util.MailClient;
import com.example.newcoder.util.RedisKeyUtil;
import com.example.newcoder.util.CommunityConstant;
import com.example.newcoder.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"all"})
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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
            clearCache(userId); // 用户状态被修改，所有删除原来的数据，下次再重新写入
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

        // 如果都没错，则生成登录凭证
        final LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(newCoderUtil.generateUUID()); // 随机,用于验证是否有有效的登陆凭证
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*expiredSeconds));
        // final int i = loginTicketMapper.insertLoginTicket(loginTicket);   存入到redis中！
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);  // 把对象序列化成字符串

        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    // 退出 ,从redis中使得登出
    public void logout(String ticket){
        // loginTicketMapper.updateStatus(ticket,1);
        final String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);   // 修改ticket的状态，存回到redis中
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }

    // 根据ticket查询的用户,从redis中查
    public LoginTicket findTicket(String ticket){
        // final LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        final String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
    }

    public int updateHeader(int userId, String headerUrl) {

        final int i = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId); // 用户的heaerUrl被修改
        return i;
    }

    public User findUserByName(String name){ return userMapper.selectByName(name);}

    public User findUserById(int id) {
        // return userMapper.selectById(id);
        User user = getCache(id);
        if (user==null){
            user=initCache(id);
        }
        return user;
    }

    // 1.设置优先从缓存中取User值；
    private User getCache(int userId){
        // 用户的key
        final String userKey = RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(userKey);
    }
    // 2.如果取不到就初始化缓存数据；
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        final String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.SECONDS);    //  存入数据和过去时间
        return user;
    }
    // 3.数据变更时，清楚缓存数据
    public void clearCache(int userId){
        final String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
