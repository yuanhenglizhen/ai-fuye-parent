package com.hanzaitu.ai.draw.support.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.enums.Action;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import com.hanzaitu.ai.draw.service.TaskService;
import com.hanzaitu.ai.draw.service.impl.QiniuService;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.support.TaskCondition;

import com.hanzaitu.ai.draw.util.ConvertUtils;
import com.hanzaitu.ai.draw.util.MessageData;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.bean.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UVMessageHandler implements MessageHandler {
	private final TaskService taskQueueService;

	private final QiniuService qiniuService;

	private final UploadLoadConfig uploadLoadConfig;

	@Override
	public void onMessageReceived(Message message) throws IOException {
		MessageData messageData = ConvertUtils.matchUVContent(message.getContentRaw());
		log.debug("UVMessageHandler.onMessageReceived1："+ JacksonUtil.toJsonString(messageData));
		if (messageData == null) {
			return;
		}
		Set<TaskStatus> taskStatusesSet = new HashSet<TaskStatus>();
		taskStatusesSet.add(TaskStatus.IN_PROGRESS);
		taskStatusesSet.add(TaskStatus.SUBMITTED);

		TaskCondition condition = new TaskCondition()
				.setKey(Objects.requireNonNull(message.getReferencedMessage()).getId() + "-" + messageData.getAction())
				.setStatusSet(taskStatusesSet);
		Task task = this.taskQueueService.findTask(condition)
				.max(Comparator.comparing(Task::getSubmitTime))
				.orElse(null);

		if (task == null) {
			return;
		}

		task.setMessageId(message.getId());
		finishTask(task, message);
		if (!StringUtils.isEmpty(task.getImageUrl()) && StringUtils.isEmpty(task.getNewImageUrl())) {
			/*String qNyImg = qiniuService.imageDownUpload(task.getImageUrl(),task.getId());
			task.setNewImageUrl(qNyImg);*/
			/*task.setPicture(qiniuService.imageDownNotUpload(task.getImageUrl(),task.getId(),task.getReqDomain(),
					true,task.getProxyHost(),task.getProxyPort()));*/
			task.setPicture(qiniuService.imageDownNotUpload(task,true));
		}

		Task sendTaskObj = new Task();
		BeanUtils.copyProperties(task,sendTaskObj);
		sendTaskObj.setPicture(putImgUrl(task.getPicture(),task.getId()));
		senSocketMsg(sendTaskObj);
		task.awake();
	}

	@Override
	public void onMessageUpdate(Message message) {
		String content = message.getContentRaw();
		log.debug("UVMessageHandler.onMessageUpdate："+ content);
		MessageData data = ConvertUtils.matchImagineContent(content);
		if (data == null) {
			data = ConvertUtils.matchUVContent(content);
		}
		if (data == null) {
			return;
		}
		String relatedTaskId = ConvertUtils.findTaskIdByFinalPrompt(data.getPrompt());
		if (CharSequenceUtil.isBlank(relatedTaskId)) {
			return;
		}
		Set<Action> actionSet = new HashSet<Action>();
		actionSet.add(Action.UPSCALE);
		actionSet.add(Action.VARIATION);

		Set<TaskStatus> taskStatusesSet = new HashSet<TaskStatus>();
		taskStatusesSet.add(TaskStatus.SUBMITTED);
		TaskCondition condition = new TaskCondition()
				.setActionSet(actionSet)
				.setRelatedTaskId(relatedTaskId)
				.setStatusSet(taskStatusesSet);
		Task task = this.taskQueueService.findTask(condition)
				.max(Comparator.comparing(Task::getSubmitTime))
				.orElse(null);
		if (task == null) {
			return;
		}
		task.setStatus(TaskStatus.IN_PROGRESS);
		task.awake();
	}

	/**
	 * 上传图片
	 * @param imgPath
	 * @param taskId
	 * @return
	 */
	private String putImgUrl(String imgPath,String taskId) {
		if (StringUtils.isNotEmpty(imgPath) && (imgPath.startsWith("/") || imgPath.startsWith("\\"))) {
			String  domain = uploadLoadConfig.getQnyImageDomain();
			Map<String,String> domainMap = taskQueueService.getTaskDomain();
			String localDomain = domainMap.get(taskId);
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
