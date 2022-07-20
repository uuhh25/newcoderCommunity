package com.example.newcoder;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash() {
        String redisKey = "test:user";

        redisTemplate.opsForHash().put(redisKey, "id",1);
        redisTemplate.opsForHash().put(redisKey, "username","hha");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    @Test
    public void testList() {
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);
        redisTemplate.opsForList().leftPush(redisKey, 104);
        redisTemplate.opsForList().leftPush(redisKey, 105);


        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
    }

    @Test
    public void testSet() {
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey, "yyy","sss","qqq","yy","yyy","hhh");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));    //随机弹出
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testZSet() {
        String redisKey = "test:student";

        redisTemplate.opsForZSet().add(redisKey,"1",20);
        redisTemplate.opsForZSet().add(redisKey,"2",30);
        redisTemplate.opsForZSet().add(redisKey,"3",40);
        redisTemplate.opsForZSet().add(redisKey,"4",50);
        redisTemplate.opsForZSet().add(redisKey,"4",50);
        redisTemplate.opsForZSet().add(redisKey,"4",60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"4"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"4"));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"4"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,10));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,10));
    }

    @Test
    public void testKeys() {
        String redisKey = "test:student";

        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.keys("*"));
    }

    // 多次访问同一个key
    @Test
    public void testBoundOperation(){
        String redisKey="test:count";
        // 根据数据类型选择不同

        BoundValueOperations boundValueOperations=redisTemplate.boundValueOps(redisKey);

        boundValueOperations.decrement();
        boundValueOperations.increment();
        boundValueOperations.append("10");
        // "1" -> "110"
        System.out.println(boundValueOperations.get());
    }

    // Redis中的编程式事务
    @Test
    public void testTransaction(){
        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String redisKey="test:tx";
                operations.multi(); // 启用事务

                operations.opsForSet().add(redisKey,"hhhh");
                operations.opsForSet().add(redisKey,"aaa");
                operations.opsForSet().add(redisKey,"hhhaaah");
                operations.opsForSet().add(redisKey,"www");

                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();   // 提交事务
            }
        });
        System.out.println(execute);
        //        String s = "123";
        //        short ss = Short.parseShort(s);
        //        System.out.println(ss);
    }

}
