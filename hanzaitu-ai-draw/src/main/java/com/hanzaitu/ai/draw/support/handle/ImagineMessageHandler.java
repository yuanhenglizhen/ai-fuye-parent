package com.hanzaitu.ai.draw.support.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import com.hanzaitu.ai.draw.service.TaskService;
import com.hanzaitu.ai.draw.service.impl.QiniuService;
import com.hanzaitu.ai.draw.support.Task;

import com.hanzaitu.ai.draw.util.ConvertUtils;
import com.hanzaitu.ai.draw.util.MessageData;
import com.hanzaitu.ai.draw.util.RequestDomainContext;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.ServletUtils;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.bean.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImagineMessageHandler implements MessageHandler {

	private final TaskService taskQueueService;

	private final QiniuService qiniuService;

	private final UploadLoadConfig uploadLoadConfig;

	@Override
	public void onMessageReceived(Message message) throws IOException {
		MessageData messageData = ConvertUtils.matchImagineContent(message.getContentRaw());
		log.debug("ImagineMessageHandler.onMessageReceived2："+ JacksonUtil.toJsonString(messageData));
		if (messageData == null) {
			return;
		}
		String taskId = ConvertUtils.findTaskIdByFinalPrompt(messageData.getPrompt());
		if (CharSequenceUtil.isBlank(taskId)) {
			return;
		}
		Task task = this.taskQueueService.getTask(taskId);
		if (task == null) {
			return;
		}
		task.setMessageId(message.getId());
		if ("Waiting to start".equals(messageData.getStatus())) {
			task.setStatus(TaskStatus.IN_PROGRESS);
		} else {
			finishTask(task, message);
			if (!StringUtils.isEmpty(task.getImageUrl()) && StringUtils.isEmpty(task.getNewImageUrl())) {
				//task.setNewImageUrl(qiniuService.imageDownUpload(task.getImageUrl(),task.getId()));

				/*task.setPicture(qiniuService.imageDownNotUpload(task.getImageUrl(),task.getId(),task.getReqDomain(),
						true,task.getProxyHost(),task.getProxyPort()));*/
				task.setPicture(qiniuService.imageDownNotUpload(task,
						true));
			}

			Task sendTaskObj = new Task();
			BeanUtils.copyProperties(task,sendTaskObj);
			sendTaskObj.setPicture(putImgUrl(task.getPicture(),task.getId()));
			senSocketMsg(sendTaskObj);
		}
		task.awake();
	}

	@Override
	public void onMessageUpdate(Message message) {
	}

	private String putImgUrl(String imgPath,String taskId) {
		if (StringUtils.isNotEmpty(imgPath) && (imgPath.startsWith("/") || imgPath.startsWith("\\"))) {
			String  domain = uploadLoadConfig.getQnyImageDomain();
			//HttpServletRequest request =  ServletUtils.getRequest();
			Map<String,String> domainMap = taskQueueService.getTaskDomain();
			String localDomain = domainMap.get(taskId);
			/*String originHost = request.getHeader("Hzt-Origin");
			String oriHost = request.getHeader("Host");
			String localDomain = StringUtils.isEmpty(originHost) ? "http://"+oriHost : originHost;*/
			String resImg = "";
			if (StringUtils.isNotEmpty(localDomain) && imgPath.startsWith("/profile")) {
				String localDomainStr = localDomain.endsWith("/") ? localDomain.substring(0,domain.length()-1) : localDomain;
				resImg = localDomainStr+"/api"+imgPath;
			}else if (StringUtils.isNotEmpty(domain)){
				String domainStr = domain.endsWith("/") ? domain.substring(0,domain.length()-1) : domain;
				resImg = domainStr + imgPath;
			}
			return resImg.equals("") ? imgPath : resImg;
		}
		return imgPath;
	}
}
