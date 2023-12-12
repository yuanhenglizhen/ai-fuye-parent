package com.hanzaitu.ai.draw;

import com.hanzaitu.ai.draw.enums.TranslateWay;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "mj")
public class ProxyProperties {

	/**
	 * discord配置.
	 */
	private final DiscordConfig discord = new DiscordConfig();

	/**
	 * 代理配置.
	 */
	private final ProxyConfig proxy = new ProxyConfig();

	/**
	 * 任务队列配置.
	 */
	private final TaskQueueConfig queue = new TaskQueueConfig();

	/**
	 * 百度翻译配置.
	 */
	private final BaiduTranslateConfig baiduTranslate = new BaiduTranslateConfig();

	/**
	 * openai配置.
	 */
	private final OpenaiConfig openai = new OpenaiConfig();
	/**
	 * 中文prompt翻译方式.
	 */
	private TranslateWay translateWay = TranslateWay.NULL;

	/**
	 * 任务状态变更回调地址.
	 */
	private String notifyHook;

	@Data
	public static class DiscordConfig {
		/**
		 * 你的服务器id.
		 */
		private String guildId;
		/**
		 * 你的频道id.
		 */
		private String channelId;
		/**
		 * 你的登录token.
		 */
		private String userToken;
		/**
		 * 你的机器人token.
		 */
		private String botToken;
		/**
		 * Midjourney机器人的名称.
		 */
		private String mjBotName = "Midjourney Bot";
		/**
		 * 调用discord接口时的user-agent.
		 */
		private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";
	}

	@Data
	public static class BaiduTranslateConfig {
		/**
		 * 百度翻译的APP_ID.
		 */
		private String appid;
		/**
		 * 百度翻译的密钥.
		 */
		private String appSecret;
	}

	@Data
	public static class OpenaiConfig {
		/**
		 * gpt的api-key.
		 */
		private String gptApiKey;
		/**
		 * 超时时间.
		 */
		private Duration timeout = Duration.ofSeconds(30);
		/**
		 * 使用的模型.
		 */
		private String model = "gpt-3.5-turbo";
		/**
		 * 返回结果的最大分词数.
		 */
		private int maxTokens = 2048;
		/**
		 * 相似度，取值 0-2.
		 */
		private double temperature = 0;
	}

	@Data
	public static class ProxyConfig {
		/**
		 * 代理host.
		 */
		private String host;

		/**
		 * 代理端口.
		 */
		private Integer port;
	}

	@Data
	public static class TaskQueueConfig {
		/**
		 * 并发数.
		 */
		private int coreSize = 3;

		/**
		 * 等待队列长度.
		 */
		private int queueSize = 10;

		/**
		 * 任务超时时间(分钟).
		 */
		private int timeoutMinutes = 3;
	}
}
