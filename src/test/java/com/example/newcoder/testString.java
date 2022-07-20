package com.example.newcoder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class testString {

    @Test
    public void test01() {
        test test = new test();
        test.change(test.str,test.ch);  // 相当于只是调用了一个函数，str修改了函数里str的指向，ch修改了引用地址的值
        System.out.println(test);
    }
    static class test{
        String str=new String("or");
        final char[] ch={'a','a','h','h'};
        public void change(String str,char[] ch){
            str="java";
            ch[0]='h';
        }

        @Override
        public String toString() {
            return "test{" +
                    "str='" + str + '\'' +
                    ", ch=" + Arrays.toString(ch) +
                    '}';
        }
    }

}
