package com.example.newcoder.util;

@SuppressWarnings({"all"})
public class RedisKeyUtil {

    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_FOLLOWEE="followee";
    private static final String PREFIX_FOLLOWER="follower";
    private static final String PREFIX_KAPTCHA="kaptcha";

    // 生成某个实体的数据库
    // like:entity:entityType:entityId -> set(userid)； 不是单纯地存储这个实体获得了多少个赞，而是通过记录哪个用户点的赞，来获得点赞数量
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE +SPLIT+ entityType+SPLIT + entityId;
    }

    // 求某一个用户的赞的数量; 生成用户的key
    // like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_ENTITY_LIKE + SPLIT + userId;
    }

    // 某个用户的关注, 用户关注的是哪种实体中的哪一个数据
    // followee:userId:entityType:(entityId) -> zser(entityId,now)，用时间进行排序
    // entityId 是具体使用的时候加上的
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }
    // 某个实体的粉丝，user的关注
    // follower:entityType:entityId:(userId) -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 用redis存验证码
    // 没太懂，这个owner是什么
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

}
