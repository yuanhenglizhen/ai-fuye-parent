package com.hanzaitu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 启动程序
 */
@SpringBootApplication
@MapperScan({
        "com.hanzaitu.**.mapper"
})
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("憨仔兔后台管理系统 启动成功！！");
    }

}
