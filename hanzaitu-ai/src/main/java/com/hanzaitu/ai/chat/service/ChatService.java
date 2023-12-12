package com.hanzaitu.ai.chat.service;

import com.hanzaitu.ai.chat.domain.dto.ChatParamDto;
import com.hanzaitu.ai.chat.domain.entity.AiChatExpiredConfigEntity;
import com.hanzaitu.ai.chat.domain.entity.AiGptModelEntity;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {

    SseEmitter chatSee(String winId, String msgId, String prompt, String model);

    AiChatExpiredConfigEntity getChatExpiredConfig();

    void cleanUpChatRecord();

    List<AiGptModelEntity> findModelList(GptKeyTypeEnum modelFind);
}
