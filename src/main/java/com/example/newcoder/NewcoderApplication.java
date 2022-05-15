package com.example.newcoder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.newcoder.dao")
public class NewcoderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewcoderApplication.class, args);
    }

}
