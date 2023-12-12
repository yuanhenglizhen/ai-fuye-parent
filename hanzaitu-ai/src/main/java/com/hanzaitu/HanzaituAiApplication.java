package com.hanzaitu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.ConfigurableApplicationContext;


@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.hanzaitu.common",
        "com.hanzaitu.ai"
})
@MapperScan({
        "com.hanzaitu.**.mapper"
})
public class HanzaituAiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(HanzaituAiApplication.class, args);
        System.out.println("憨仔兔AI工具启动成功！！ {}");
    }

}