package com.hanzaitu.ai.draw.service.impl;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import com.hanzaitu.ai.draw.ProxyProperties;
import com.hanzaitu.ai.draw.domain.Message;
import com.hanzaitu.ai.draw.enums.ZoomEnum;
import com.hanzaitu.ai.draw.service.DiscordService;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.common.utils.StringUtils;
import eu.maxschuster.dataurl.DataUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {
	private final ProxyProperties properties;

	private static final String DISCORD_API_URL = "https://discord.com/api/v9/interactions";

	private String userAgent;

	private String imagineParamsJson;

	private String upscaleParamsJson;

	private String variationParamsJson;

	private String resetParamsJson;

	private String describeParamsJson;

	private String shortenParamsJson;

	/**
	 * 风格更加个性化参数(激进风格)
	 */
	private String highVariationParamsJson;


	/**
	 * 风格更加个性化参数(保守风格)
	 */
	private String lowVariationParamsJson;

	/**
	 * 镜头参数
	 */
	private String zoomParamsJson;



	//当依赖注入完成后用于执行初始化的方法，并且只会被执行一次
	@PostConstruct
	void init() {
		this.userAgent = this.properties.getDiscord().getUserAgent();
		this.imagineParamsJson = ResourceUtil.readUtf8Str("api-params/imagine.json");
		this.upscaleParamsJson = ResourceUtil.readUtf8Str("api-params/upscale.json");
		this.variationParamsJson = ResourceUtil.readUtf8Str("api-params/variation.json");
		this.resetParamsJson = ResourceUtil.readUtf8Str("api-params/reset.json");
		this.describeParamsJson = ResourceUtil.readUtf8Str("api-params/describe.json");
		this.shortenParamsJson = ResourceUtil.readUtf8Str("api-params/shorten.json");
		this.highVariationParamsJson = ResourceUtil.readUtf8Str("api-params/high_variation.json");
		this.lowVariationParamsJson = ResourceUtil.readUtf8Str("api-params/low_variation.json");
		this.zoomParamsJson = ResourceUtil.readUtf8Str("api-params/zoom.json");
	}

	@Override
	public Message<Void> imagine(Task task) {
		String prompt = task.getFinalPrompt();
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();
		//组合请求参数
		String paramsStr = this.imagineParamsJson.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId);

		JSONObject params = new JSONObject(paramsStr);
		params.getJSONObject("data").getJSONArray("options").getJSONObject(0)
				.put("value", prompt);
		return postJsonAndCheckStatus(params.toString(),task);
	}

	@Override
	public Message<Void> upscale(Task task, String messageId, int index, String messageHash) {
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();

		String paramsStr = this.upscaleParamsJson.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId)
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		return postJsonAndCheckStatus(paramsStr,task);
	}

	@Override
	public Message<Void> variation(Task task, String messageId, int index, String messageHash) {
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();

		String paramsStr = this.variationParamsJson.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId)
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		return postJsonAndCheckStatus(paramsStr,task);
	}

	@Override
	public Message<Void> reset(Task task,String messageId, String messageHash) {
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();

		String paramsStr = this.resetParamsJson.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId)
				.replace("$message_id", messageId)
				.replace("$message_hash", messageHash);
		return postJsonAndCheckStatus(paramsStr,task);
	}

	@Override
	public Message<String> upload(Task task,String fileName, DataUrl dataUrl) {

		try {
			String discordChannelId = task.getChannelId();

			JSONObject fileObj = new JSONObject();
			fileObj.put("filename", fileName);
			fileObj.put("file_size", dataUrl.getData().length);
			fileObj.put("id", "0");
			JSONObject params = new JSONObject()
					.put("files", new JSONArray().put(fileObj));
			String discordUploadUrl = "https://discord.com/api/v9/channels/" + discordChannelId + "/attachments";

			ResponseEntity<String> responseEntity = postJson(discordUploadUrl, params.toString(), task);
			if (responseEntity.getStatusCode() != HttpStatus.OK) {
				log.error("上传图片到discord失败, status: {}, msg: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
				return Message.of(Message.VALIDATION_ERROR_CODE, "上传图片到discord失败");
			}
			JSONArray array = new JSONObject(responseEntity.getBody()).getJSONArray("attachments");
			if (array.length() == 0) {
				return Message.of(Message.VALIDATION_ERROR_CODE, "上传图片到discord失败");
			}
			String uploadUrl = array.getJSONObject(0).getString("upload_url");
			String uploadFilename = array.getJSONObject(0).getString("upload_filename");
			putFile(uploadUrl, dataUrl, task);
			return Message.success(uploadFilename);
		} catch (Exception e) {
			log.error("上传图片到discord失败", e);
			return Message.of(Message.FAILURE_CODE, "上传图片到discord失败");
		}
	}

	@Override
	public Message<Void> describe(Task task,String finalFileName) {
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();
		String randomNum = RandomUtil.randomNumbers(19);

		String fileName = CharSequenceUtil.subAfter(finalFileName, "/", true);
		String paramsStr = this.describeParamsJson.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId)
				.replace("$file_name", fileName)
				.replace("$final_file_name", finalFileName).replace("$nonce",randomNum);
		return postJsonAndCheckStatus(paramsStr, task);
	}

	/**
	 * 优化关键词
	 * @param task
	 * @return
	 */
	@Override
	public Message<Void> shorten(Task task) {
		String prompt = task.getFinalPrompt();
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();
		String randomNum = RandomUtil.randomNumbers(19);
		//组合请求参数
		String paramsStr = this.shortenParamsJson.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId).replace("$prompt",prompt)
				.replace("$nonce",randomNum);

		return postJsonAndCheckStatus(paramsStr, task);
	}

	/**
	 * 激进风格相似图片
	 * @param task
	 * @param messageId
	 * @param messageHash
	 * @return
	 */
	@Override
	public Message<Void> highVariation(Task task, String messageId, String messageHash) {
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();
		String randomNum = RandomUtil.randomNumbers(19);

		String paramsStr = this.highVariationParamsJson
				.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId)
				.replace("$nonce",randomNum)
				.replace("$message_id",messageId)
				.replace("$message_hash",messageHash);

		return postJsonAndCheckStatus(paramsStr, task);
	}

	/**
	 * 保守风格相似图片
	 * @param task
	 * @param messageId
	 * @param messageHash
	 * @return
	 */
	@Override
	public Message<Void> lowVariation(Task task, String messageId, String messageHash) {
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();
		String randomNum = RandomUtil.randomNumbers(19);

		String paramsStr = this.lowVariationParamsJson
				.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId)
				.replace("$nonce",randomNum)
				.replace("$message_id",messageId)
				.replace("$message_hash",messageHash);
		return postJsonAndCheckStatus(paramsStr, task);
	}

	/**
	 * zoom镜头拉近
	 * @param task
	 * @param messageId
	 * @param messageHash
	 * @return
	 */
	@Override
	public Message<Void> zoom(Task task, String messageId, String messageHash, ZoomEnum zoomEnum) {
		String discordGuildId = task.getGuildId();
		String discordChannelId = task.getChannelId();
		String randomNum = RandomUtil.randomNumbers(19);
		Integer zoomNum = zoomEnum.getValue();
		String paramsStr = this.zoomParamsJson
				.replace("$guild_id", discordGuildId)
				.replace("$channel_id", discordChannelId)
				.replace("$nonce",randomNum)
				.replace("$message_hash",messageHash)
				.replace("$message_id",messageId)
				.replace("$zoom", zoomNum.toString());
		//throw new RuntimeException("test");
		return postJsonAndCheckStatus(paramsStr, task);
	}

	/**
	 * 上传文件discord官网
	 * @param uploadUrl
	 * @param dataUrl
	 * @param task
	 */
	private void putFile(String uploadUrl, DataUrl dataUrl, Task task) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", this.userAgent);
		headers.setContentType(MediaType.valueOf(dataUrl.getMimeType()));
		headers.setContentLength(dataUrl.getData().length);
		HttpEntity<byte[]> requestEntity = new HttpEntity<>(dataUrl.getData(), headers);
		getRestTemplate(task).put(uploadUrl, requestEntity);
	}

	private ResponseEntity<String> postJson(String paramsStr, Task task) {
		return postJson(DISCORD_API_URL, paramsStr, task);
	}

	private ResponseEntity<String> postJson(String url, String paramsStr, Task task) {
		String userToken = task.getUserToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", userToken);
		headers.add("User-Agent", this.userAgent);
		HttpEntity<String> httpEntity = new HttpEntity<>(paramsStr, headers);
		return  getRestTemplate(task).postForEntity(url, httpEntity, String.class);
	}

	private Message<Void> postJsonAndCheckStatus(String paramsStr, Task task) {
		try {
			//发起接口请求
			log.debug("发起接口请求："+paramsStr);
			ResponseEntity<String> responseEntity = postJson(paramsStr, task);
			if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
				return Message.success();
			}
			RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes());
			return Message.of(responseEntity.getStatusCodeValue(), CharSequenceUtil.sub(responseEntity.getBody(),
					0, 100));
		} catch (HttpClientErrorException e) {
			try {
				JSONObject error = new JSONObject(e.getResponseBodyAsString());
				return Message.of(error.optInt("code", e.getRawStatusCode()), error.optString("message"));
			} catch (Exception je) {
				return Message.of(e.getRawStatusCode(), CharSequenceUtil.sub(e.getMessage(), 0, 100));
			}
		}
	}

	/**
	 * 设置代理
	 * @param task
	 * @return
	 */
	private RestTemplate getRestTemplate(Task task) {
		RestTemplate restTemplate = new RestTemplate();
		//如果有代理就设置代理
		if (StringUtils.isNotEmpty(task.getProxyHost()) && task.getProxyPort() != null) {
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setProxy(
					new Proxy(
							Proxy.Type.HTTP,
							//设置代理服务
							new InetSocketAddress(task.getProxyHost(), task.getProxyPort())
					)
			);
			restTemplate.setRequestFactory(requestFactory);
		}
		return restTemplate;
	}

}
