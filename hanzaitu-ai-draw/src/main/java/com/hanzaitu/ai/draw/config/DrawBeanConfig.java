package com.hanzaitu.ai.draw.config;


import com.hanzaitu.ai.draw.ProxyProperties;
import com.hanzaitu.ai.draw.service.TranslateService;
import com.hanzaitu.ai.draw.service.impl.BaiduTranslateServiceImpl;
import com.hanzaitu.ai.draw.service.impl.GPTTranslateServiceImpl;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.common.core.chat.KeyManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class DrawBeanConfig {


	@Autowired
	private KeyManagerFactory keyManager;


	@Bean
	TranslateService translateService(ProxyProperties properties) {
		switch (properties.getTranslateWay()) {
			case BAIDU:
				return  new BaiduTranslateServiceImpl(properties.getBaiduTranslate());
			case GPT:
				return new GPTTranslateServiceImpl(keyManager);
			default:
				return prompt -> prompt;
		}
	}

	@Bean
	RedisTemplate<String, Task> taskRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Task> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		return redisTemplate;
	}

}
