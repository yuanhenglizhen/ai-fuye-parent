package com.hanzaitu.ai.draw.support.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import com.hanzaitu.ai.draw.service.TaskService;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.support.WsSessionManager;
import com.hanzaitu.common.utils.DateUtils;
import com.hanzaitu.common.utils.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
@Slf4j
@Component
@RequiredArgsConstructor
public class DescribeMessageHandler implements MessageHandler {

	private final TaskService taskQueueService;

	@Override
	public void onMessageReceived(Message message) {
	}

	@Override
	public void onMessageUpdate(Message message) throws IOException {
		log.debug("DescribeMessageHandler.onMessageUpdate1："+message.getContentRaw());
		List<MessageEmbed> embeds = message.getEmbeds();
		if (embeds.isEmpty()) {
			return;
		}
		String prompt = embeds.get(0).getDescription();
		String imageUrl = Objects.requireNonNull(embeds.get(0).getImage()).getUrl();
		assert imageUrl != null;
		int hashStartIndex = imageUrl.lastIndexOf("/");
		String taskId = CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
		Task task = this.taskQueueService.getTask(taskId);
		log.debug("DescribeMessageHandler.onMessageUpdate3："+task);
		if (task == null) {
			return;
		}
		task.setMessageId(message.getId());
		task.setPrompt(prompt);
		task.setPromptEn(prompt);
		task.setPromptUserRaw(prompt);
		task.setImageUrl(imageUrl);
		task.setFinishTime(DateUtils.getTime());
		task.setStatus(TaskStatus.SUCCESS);
		task.setProgress(100);
		task.setMjRawPrompt(message.getContentRaw());
		senSocketMsg(task);
		//this.taskQueueService.removeRunTask(task);
		task.awake();
	}

}
