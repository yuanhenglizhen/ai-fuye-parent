package com.hanzaitu.ai.draw.service;

import com.hanzaitu.ai.draw.domain.Message;
import com.hanzaitu.ai.draw.domain.dto.DrawQuadratPraiseDto;
import com.hanzaitu.ai.draw.domain.dto.ReleaseDrawDto;
import com.hanzaitu.ai.draw.enums.ZoomEnum;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.support.TaskCondition;
import com.hanzaitu.common.core.domain.AjaxResult;
import eu.maxschuster.dataurl.DataUrl;

import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public interface TaskService {

	Task getTask(String id);

	Stream<Task> findTask(TaskCondition condition);

	Message<String> submitImagine(Task task);

    Message<String> submitShorten(Task task);

    Message<String> submitUpscale(Task task, String targetMessageId, String targetMessageHash, int index);

	Message<String> submitVariation(Task task, String targetMessageId, String targetMessageHash, int index);

	Message<String> submitDescribe(Task task, DataUrl dataUrl);

    Message<String> submitHighVariation(Task task, String targetMessageId, String targetMessageHash);


	Message<String> submitLowVariation(Task task, String targetMessageId, String targetMessageHash);


    Message<String> submitZoom(Task task, String messageId, String messageHash, ZoomEnum zoomEnum);

    void removeRunTask(Task task);

    AjaxResult releaseDraw(ReleaseDrawDto releaseDrawDto);

    AjaxResult collect(ReleaseDrawDto releaseDrawDto);

    ConcurrentMap<String,String> getTaskDomain();
}