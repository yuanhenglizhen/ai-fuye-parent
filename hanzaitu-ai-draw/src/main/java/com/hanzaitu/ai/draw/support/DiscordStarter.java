package com.hanzaitu.ai.draw.support;

import com.hanzaitu.ai.draw.ProxyProperties;
import com.hanzaitu.ai.draw.domain.entity.DiscordAuthTokenEntity;
import com.hanzaitu.ai.draw.mapper.AiDrawKeyMapper;
import com.hanzaitu.common.manager.AsyncManager;
import com.hanzaitu.common.utils.SpringApplicationUtils;
import com.hanzaitu.common.utils.StringUtils;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocketFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.TimerTask;

@Slf4j
@Component
public class DiscordStarter implements ApplicationListener<ApplicationStartedEvent> {

	@Resource
	private ProxyProperties properties;

	@Resource
	private DiscordMessageListener discordMessageListener;

	@Autowired
	private AiDrawKeyMapper aiDrawKeyMapper;

	@Async
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		if(SpringApplicationUtils.getDevMode())
		{
			return;
		}
		List<DiscordAuthTokenEntity> authTokenEntityResList = aiDrawKeyMapper.selectDrawKeyList();
		for(DiscordAuthTokenEntity d :authTokenEntityResList) {
			log.debug("onApplicationEvent-开始连接discover,id :{}", d.getTokenId());
			DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(d.getBotToken(),
					GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);

			builder.addEventListeners(this.discordMessageListener);
			if (StringUtils.isNotEmpty(d.getProxyHost()) && d.getProxyPort() != null) {

				log.debug("识别到代理 {}:{}", d.getProxyHost(),d.getProxyPort());
				System.setProperty("http.proxyHost", d.getProxyHost());
				System.setProperty("http.proxyPort", String.valueOf(d.getProxyPort()));
				System.setProperty("https.proxyHost", d.getProxyHost());
				System.setProperty("https.proxyPort", String.valueOf(d.getProxyPort()));

				WebSocketFactory webSocketFactory = new WebSocketFactory();
				ProxySettings proxySettings = webSocketFactory.getProxySettings();
				proxySettings.setHost(d.getProxyHost());
				proxySettings.setPort(d.getProxyPort());
				//proxySettings.setPassword("teddysun.com");
				builder.setWebsocketFactory(webSocketFactory);
			}
			builder.build();
			log.debug("onApplicationEvent-discover连接成功: id:{}", d.getTokenId());
		}
	}

}