package com.example.newcoder;

import com.example.newcoder.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionTest {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void test01(){
        final Object o = alphaService.save1();
        System.out.println(o);
    }
    @Test
    public void test02(){
        final Object o = alphaService.save2();
        System.out.println(o);

    }

}
