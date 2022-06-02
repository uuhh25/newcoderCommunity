package com.example.newcoder.service;

import com.example.newcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@SuppressWarnings({"all"})
@Service
public class LikeService {

    // 将点赞的数据存到redis中
    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞,特指要传入entityUserId
    public void like(int userId,int entityType,int entityId,int entityUserId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        // 判断点赞的状态
        Boolean isLiked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if(isLiked){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }

        // 开启redis事务
//        redisTemplate.execute(new SessionCallback() {
//            @Override
//            public Object execute(RedisOperations operations) throws DataAccessException {
//                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
//                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId); // 被赞的这个人
//
//                // 判断点赞的状态
//                Boolean isLiked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//                operations.multi();
//                // 判断是否需要修改点赞状态,已经修改被点赞用户赞的数量
//                if(isLiked){
//                    operations.opsForSet().remove(entityLikeKey,userId);
//                    operations.opsForValue().decrement(userLikeKey);
//                }else {
//                    operations.opsForSet().add(entityLikeKey,userId);
//                    operations.opsForValue().increment(userLikeKey);
//                }
//                return operations.exec();
//            }
//        });
    }

    // 统计点赞数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 点赞的状态,用int表示更多的点赞状态..而不是用boolean
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        // 判断点赞的状态
        Boolean isLiked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isLiked){
            return 1;
        }else {
            return 0;
        }
    }

}
