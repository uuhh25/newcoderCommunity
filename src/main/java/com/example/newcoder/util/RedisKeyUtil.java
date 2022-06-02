package com.example.newcoder.util;

@SuppressWarnings({"all"})
public class RedisKeyUtil {

    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";

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

}
