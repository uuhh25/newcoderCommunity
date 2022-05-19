package com.example.newcoder.util;

import com.example.newcoder.entity.User;
import org.springframework.stereotype.Component;

@SuppressWarnings({"all"})
/**
持有用户信息，用于代替session对象，为了线程安全；
 * */
@Component
public class HostHolder {
    // 以线程为key，存取到相应的map
    private ThreadLocal<User> users=new ThreadLocal<>();

    // 对存入的user进行处理和垃圾清理
    public void setUsers(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
