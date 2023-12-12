package com.hanzaitu.ai.draw.service.impl;


import cn.hutool.core.text.CharSequenceUtil;
import com.hanzaitu.ai.draw.service.TranslateService;
import com.hanzaitu.common.core.chat.KeyManagerFactory;
import com.hanzaitu.common.domain.ChatKeyEntity;
import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.hanzaitu.common.utils.StringUtils;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GPTTranslateServiceImpl implements TranslateService {

	private KeyManagerFactory keyManagerFactory;

	public GPTTranslateServiceImpl(KeyManagerFactory openaiConfig) {
		this.keyManagerFactory = openaiConfig;
	}

	@Override
	public String translateToEnglish(String prompt) {
		if (!containsChinese(prompt)) {
			return prompt;
		}
		ChatKeyEntity key = keyManagerFactory.getKey(GptKeyTypeEnum.GPT_3);
		if (key == null || key.getChatKey() == null) {
			log.error("翻译失败，CHAT没有可以用的：key");
			return prompt;
		}
		Proxy proxy = Proxy.NO_PROXY;
		//如果不为空就代表需要代理
		if(StringUtils.isNotEmpty(key.getProxyHost()) && key.getProxyPort() != null) {
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(key.getProxyHost(),
					Integer.parseInt(key.getProxyPort())));
		}
		try {
			ChatGPT chatGPT = ChatGPT.builder()
					.apiKey(key.getChatKey())
					.timeout(50)
					.proxy(proxy)
					.build()
					.init();
			log.debug("发起请求中");
			List<Message> messages = new ArrayList<>();
			Message sysPrompt = Message.ofSystem("translate Chinese to English");
			Message userPrompt = Message.of(prompt);
			messages.add(sysPrompt);
			messages.add(userPrompt);

			ChatCompletion chatCompletion = ChatCompletion.builder()
					.maxTokens(key.getMaxToken())
					.model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
					.temperature(key.getTemperature())
					.messages(messages)
					.build();

			ChatCompletionResponse completion = chatGPT.chatCompletion(chatCompletion);
			Message chatRes = completion.getChoices().get(0).getMessage();
			return chatRes.getContent();
		} catch (Exception e) {
			log.error("翻译失败，API调用出错：{}", e);
			return prompt;
		}
	}

}