package com.hanzaitu.ai.draw.service;


import com.hanzaitu.ai.draw.domain.Message;
import com.hanzaitu.ai.draw.enums.ZoomEnum;
import com.hanzaitu.ai.draw.support.Task;
import eu.maxschuster.dataurl.DataUrl;

public interface DiscordService {

	Message<Void> imagine(Task task);

	Message<Void> upscale(Task task,String messageId, int index, String messageHash);

	Message<Void> variation(Task task, String messageId, int index, String messageHash);

    Message<Void> reset(Task task,String messageId, String messageHash);

	Message<String> upload(Task task,String fileName, DataUrl dataUrl);

	Message<Void> describe(Task task,String finalFileName);

	Message<Void> shorten(Task task);

	Message<Void> highVariation(Task task, String messageId, String messageHash);

	Message<Void> lowVariation(Task task, String messageId, String messageHash);

	Message<Void> zoom(Task task, String messageId, String messageHash, ZoomEnum zoomEnum);
}
