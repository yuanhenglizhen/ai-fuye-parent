package com.hanzaitu.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //可以被跨域的路径
                .allowedOrigins("*") //域名的白名单
                .allowedMethods("*")/*"GET", "POST", "DELETE", "PUT"*/
                .allowedHeaders("*") //允许所有的请求header访问，可以自定义设置任意请求头信息
                .maxAge(3600); //这个复杂请求是预检用的，设置预检多久失效
    }
}
