package com.example.newcoder.controller;

import com.example.newcoder.entity.Event;
import com.example.newcoder.entity.Page;
import com.example.newcoder.entity.User;
import com.example.newcoder.event.EventProducer;
import com.example.newcoder.service.FollowService;
import com.example.newcoder.service.UserService;
import com.example.newcoder.util.HostHolder;
import com.example.newcoder.util.CommunityConstant;
import com.example.newcoder.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"all"})
@Controller
public class FollowController implements CommunityConstant { // 异步操作
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;


    // 关注
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){  // entityType我们写了一个全局变量
        // 先要从当前用户种获取id
        User user = hostHolder.getUser();
        if (user==null){  //不需要做了，已经有一个loginRequiredInterceptor了
            return "/site/login";
        }
        followService.follow(user.getId(),entityType,entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return newCoderUtil.getJSONString(0,"关注成功");
    }

    // 取关
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){  // entityType我们写了一个全局变量
        // 先要从当前用户种获取id
        User user = hostHolder.getUser();
        //        if (user==null){  不需要做了，已经有一个loginRequiredInterceptor了
        //            return "/site/login";
        //        }
        followService.unfollow(user.getId(),entityType,entityId);

        return newCoderUtil.getJSONString(0,"取消关注成功");
    }

    // 查询某个用户的关注列表
    @RequestMapping(value = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model){
        final User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在");
        }
        // 页面是根据当前查询的用户来显示的，所以要把我们当前查询的用户传递到页面上
        model.addAttribute("followeeUser",user);
        // 分页的设置
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int) followService.findFollowCount(userId, ENTITY_USER));

        // 关注列表，当前登录（查询）用户对该列表实体的关注状态
        List<Map<String,Object>> followeeList=followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if (followeeList!=null){
            for (Map<String, Object> map : followeeList) {
                User followee= (User) map.get("user");
                // 把当前查询用户对列表中用户关注状态存进去
                map.put("hasFollowed",hasFollowed(followee.getId()));
            }
        }
        model.addAttribute("followees",followeeList);
        return "/site/followee";
    }

    // 查询某个用户的粉丝列表
    @RequestMapping(value = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Page page, Model model){
        final User user = userService.findUserById(userId);
        if (user==null){
            throw new RuntimeException("该用户不存在");
        }
        // 页面是根据当前查询的用户来显示的，所以要把我们当前查询的用户传递到页面上
        model.addAttribute("followerUser",user);
        // 分页的设置
        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int) followService.findFollowerCount(userId,ENTITY_USER));

        // 关注列表，当前登录（查询）用户对该列表实体的关注状态
        List<Map<String,Object>> followerList=followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if (followerList!=null){
            for (Map<String, Object> map : followerList) {
                User follower= (User) map.get("user");
                // 把当前查询用户对列表中用户关注状态存进去
                map.put("hasFollowed",hasFollowed(follower.getId()));
            }
        }
        model.addAttribute("followers",followerList);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser()==null) {
            return false;
        }
        // 当前用户，对列表中的具体实体的关注状态
        return followService.hasFollowed(hostHolder.getUser().getId(), CommunityConstant.ENTITY_USER,userId);
    }
}
