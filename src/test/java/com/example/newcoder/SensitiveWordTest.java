package com.example.newcoder;

import com.example.newcoder.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveWordTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test01(){
        String t="来嫖娼啦~，哈哈哈哈；还是**赌*博*呢？去下赌博";
        final String filter = sensitiveFilter.filter(t);
        System.out.println(filter);

        String text = "这里可以读博,可以嫖娼,可以吸毒, 可以***...";
        text  =  sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以赌→博→,可以→嫖→娼→,可以吸→毒, 可以→***→...fabc";
        text  =  sensitiveFilter.filter(text);
        System.out.println(text);

        text = "fabcd";
        text  =  sensitiveFilter.filter(text);
        System.out.println(text);

        text = "fabcc";
        text  =  sensitiveFilter.filter(text);
        System.out.println(text);

        text = "fabc";
        text  =  sensitiveFilter.filter(text);
        System.out.println(text);
    }

}
