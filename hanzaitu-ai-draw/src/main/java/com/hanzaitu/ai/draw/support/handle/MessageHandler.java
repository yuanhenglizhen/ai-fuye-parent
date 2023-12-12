package com.hanzaitu.ai.draw.support.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.support.WsSessionManager;
import com.hanzaitu.ai.draw.util.FileUtil;
import com.hanzaitu.common.utils.DateUtils;
import com.hanzaitu.common.utils.JacksonUtil;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface MessageHandler {

	void onMessageReceived(Message message) throws IOException;

	void onMessageUpdate(Message message) throws IOException;

	default void finishTask(Task task, Message message) throws IOException {
		task.setFinishTime(DateUtils.getTime());
		if (!message.getAttachments().isEmpty()) {
			task.setStatus(TaskStatus.SUCCESS);
			String imageUrl = message.getAttachments().get(0).getUrl();
			task.setImageUrl(imageUrl);
			int hashStartIndex = imageUrl.lastIndexOf("_");
			task.setMessageHash(CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true));
		} else {
			task.setStatus(TaskStatus.FAILURE);
		}
		task.setMjRawPrompt(message.getContentRaw());
		task.setProgress(100);
	}


	default void senSocketMsg(Task task)  {
		System.out.println(String.format("DiscordMessageListener.onMessageUpdate1 - {}: {}", "发送客户端", JacksonUtil.toJsonString(task)));
		WebSocketSession webSocketSession = WsSessionManager.get(task.getUserId());
		if (webSocketSession != null) {
			try {


				String jsons = JacksonUtil.toJsonString(task);
				synchronized(webSocketSession) {
					webSocketSession.sendMessage(new TextMessage(jsons));
				}
		 	} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("发送对象为空");
		}
	}


}
