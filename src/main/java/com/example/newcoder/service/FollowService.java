package com.example.newcoder.service;

import com.example.newcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Date;

@SuppressWarnings({"all"})
@Service
public class FollowService {

    // 用redis存储关注和粉丝
    @Autowired
    private RedisTemplate redisTemplate;

    // 用户的关注的实体数量
    public long findFollowCount(int userId,int entityType){
        //
        String followee=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followee);
    }

    // 某一个具体的实体的粉丝数量
    public long findFollowerCount(int entityId,int entityType){
        //
        String follower=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(follower);
    }

    // 查询用户是否关注 某个 具体的 实体 [实体类型+实体id]
    public boolean hasFollowed(int userId, int entityType,int entityId){
        // 用户关注的所有实体里面，是否又entityId这个一个
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;

    }

    // 关注; 哪个用户 关注的 哪个实体 的 哪个数据
    public void follow(int userId, int entityType,int entityId){
        // 1. 获得redis的key
        // 2. 关注的目标，被关注的实体
        // 因为关注数 和 粉丝数是要同时变化的，所以得写在一个事务中
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
                // 存储关注和被关注
                operations.multi(); // 启用事务

                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                operations.exec();  // 结束事务

                return null;
            }
        });
    }

    // 取消关注
    public void unfollow(int userId, int entityType,int entityId){
        // 1. 获得redis的key
        // 2. 关注的目标，被关注的实体
        // 因为关注数 和 粉丝数是要同时变化的，所以得写在一个事务中
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
                // 存储关注和被关注
                operations.multi(); // 启用事务

                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                operations.exec();  // 结束事务

                return null;
            }
        });
    }
}
