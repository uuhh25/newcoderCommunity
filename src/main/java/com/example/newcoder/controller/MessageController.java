package com.example.newcoder.controller;


import com.example.newcoder.entity.Message;
import com.example.newcoder.entity.Page;
import com.example.newcoder.entity.User;
import com.example.newcoder.service.MessageService;
import com.example.newcoder.service.UserService;
import com.example.newcoder.util.HostHolder;
import com.example.newcoder.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@SuppressWarnings({"all"})
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;  // 用于调取当前用户信息

    // 私信列表
    @RequestMapping(value = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        if (user==null){
            return "/site/login";
        }
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<Message> conversationsList = messageService.
                findConversations(user.getId(), page.getOffset(), page.getLimit());
        // 继续用map，去装在每一条会话对应是数据
        List<Map<String,Object>> conversations=new ArrayList<>();
        if (conversationsList!=null){

            for (Message message : conversationsList) {
                Map<String, Object> map = new HashMap<>();
                // 会话、私信数量、未读私信数量、
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                // 会话中的对方信息
                int targetId = user.getId() == message.getFromId() ? message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        // 查询未读消息数量
        final int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnReadCount",letterUnreadCount);

        return "/site/letter";
    }

    // 私信详情,某个会话详情+分页；
    @RequestMapping(value = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        User user = hostHolder.getUser();
        if (user==null){
            return "/index";
        }
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" +conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 一个会话的所有消息
        List<Message> lettersList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        // 依然是用一个容器来存详情
        List<Map<String,Object>> letters=new ArrayList<>();
        if (lettersList!=null){
            for (Message letter : lettersList) {
                Map<String,Object> map=new HashMap<>();
                map.put("letter",letter);
                // 存清楚发送方和接收方
                if (hostHolder.getUser().getId() != letter.getFromId()){
                    map.put("fromUser",userService.findUserById(letter.getFromId()));
                    map.put("toUser",null);
                }else {
                    // 接收方
                    map.put("fromUser",null);
                    map.put("toUser",userService.findUserById(hostHolder.getUser().getId()));
                }

                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        // 从conversationId获取当前发送私信的用户
        model.addAttribute("target",generateUser(conversationId));

        // 设置已读
        List<Integer> ids=getLettersIds(lettersList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User generateUser(String conversationId){
        String[] ids=conversationId.split("_");
        int d0=Integer.parseInt(ids[0]);
        int d1=Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId()==d0){
            return userService.findUserById(d1);
        }
        else {
            return userService.findUserById(d0);
        }
    }

    // 发送私信的请求
    @RequestMapping(value = "/letter/send",method = RequestMethod.POST)
    @ResponseBody   // 异步
    public String sendLetter(String toName,String content){
        // 通过name找user的信息
        User target = userService.findUserByName(toName);
        // 空值处理
        if (target==null){
            return newCoderUtil.getJSONString(1,"目标用户不存在");
        }

        Message message=new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        // 小的id在前
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+ message.getToId());
        }else {
            message.setConversationId(message.getToId()+"_"+ message.getFromId());
        }
        message.setStatus(0);
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return newCoderUtil.getJSONString(0);   // 没有报错，则返回0
    }

    // 获取可能要修改状态的ids
    private List<Integer> getLettersIds(List<Message> letterList){
        List<Integer> ids=new ArrayList<>();
        if (letterList!=null){
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());   // 消息的id
                }
            }
        }
        return ids;
    }

}
