package com.hanzaitu.ai.draw.support;

import com.hanzaitu.ai.draw.ProxyProperties;
import com.hanzaitu.ai.draw.service.DbTaskStoreService;
import com.hanzaitu.ai.draw.service.TaskService;
import com.hanzaitu.ai.draw.support.handle.DescribeMessageHandler;
import com.hanzaitu.ai.draw.support.handle.ImagineMessageHandler;
import com.hanzaitu.ai.draw.support.handle.UVMessageHandler;
import com.hanzaitu.ai.draw.util.ConvertUtils;
import com.hanzaitu.ai.draw.util.MessageData;
import com.hanzaitu.common.utils.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {

	private final ProxyProperties properties;
	private final ImagineMessageHandler imagineMessageHandler;
	private final UVMessageHandler uvMessageHandler;
	private final DescribeMessageHandler describeMessageHandler;
	private final DbTaskStoreService taskStoreService;
	private final TaskService taskQueueService;


	private boolean ignoreAndLogMessage(Message message, String eventName) {
		String channelId = message.getChannel().getId();
		TaskCondition condition = new TaskCondition().setChannelId(channelId);
		if (this.taskQueueService.findTask(condition).count() == 0) {
			return true;
		}

		String authorName = message.getAuthor().getName();
		log.debug("{} - {}: {}", eventName, authorName, message.getContentRaw());
		return !this.properties.getDiscord().getMjBotName().equals(authorName);
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {Message message = event.getMessage();
		log.debug("DiscordMessageListener.onMessageUpdate 原始消息：{}",event.getMessage().toString());
		//过滤掉非和预期配置参数不相符的配置
		if (ignoreAndLogMessage(message, "消息变更")) {
			return;
		}
		if (message.getInteraction() != null && "describe".equals(message.getInteraction().getName())) {
			try {
				this.describeMessageHandler.onMessageUpdate(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			this.uvMessageHandler.onMessageUpdate(message);
		}

		String contentRaw = message.getContentRaw();
		MessageData messageData = ConvertUtils.matchImagineContent(contentRaw);
		if (messageData != null) {
			String taskId = ConvertUtils.findTaskIdByFinalPrompt(messageData.getPrompt());
			log.debug("收到taskId:{} 原始消息：{}",taskId,messageData.getPrompt());
			Integer progress = ConvertUtils.matchImagineContentProgress(contentRaw);
			log.debug("发送进度："+progress);
			Task task = taskQueueService.getTask(taskId);
			task.setMjRawPrompt(contentRaw);
			task.setProgress(progress == null ? 0 : progress);
			this.describeMessageHandler.senSocketMsg(task);
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		log.debug("DiscordMessageListener.onMessageReceived 原始消息：{}",event.getMessage().toString());
		Message message = event.getMessage();
		if (ignoreAndLogMessage(message, "消息接收")) {
			return;
		}
		try {
			if (MessageType.SLASH_COMMAND.equals(message.getType()) || MessageType.DEFAULT.equals(message.getType())) {
				this.imagineMessageHandler.onMessageReceived(message);
			} else if (MessageType.INLINE_REPLY.equals(message.getType()) && message.getReferencedMessage() != null) {
				this.uvMessageHandler.onMessageReceived(message);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
