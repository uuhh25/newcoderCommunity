package com.example.newcoder.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
@SuppressWarnings({"all"})
@Configuration
public class KaptchaConfig {

    //
    @Bean
    public Producer kaptchaProducer(){
        // 设置随机的图片
        final Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string","0123456789qwertyuiopasdfghjklzxcvbnm"); // 随机字符的范围
        properties.setProperty("kaptcha.textproducer.char.length","4"); // 随机字符的长度范围
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise"); // 什么噪声类，给图片的处理

        final DefaultKaptcha kaptcha = new DefaultKaptcha();
        final Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
