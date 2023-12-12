package com.hanzaitu.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfig {

    /**
     * 单例对象映射器
     * ObjectMapper 将jackson的这个核心类注册为单例的Bean,防止在每一次序列化和反序列化时 new新的对象
     * @return {@link ObjectMapper}
     */
    //@Bean
    public ObjectMapper singletonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();//可解决时间格式等一系列问题
        return objectMapper;
    }
}