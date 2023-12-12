package com.hanzaitu.ai.chat.service;

import com.hanzaitu.ai.chat.domain.dto.ChatHistoryContentDto;
import com.hanzaitu.ai.chat.domain.dto.ChatHistoryDto;
import com.hanzaitu.ai.chat.domain.vo.ChatHistoryContentVo;
import com.hanzaitu.ai.chat.domain.vo.ChatHistoryTitleVo;

import java.util.List;


public interface ChatRecordService {

    List<ChatHistoryTitleVo> selectHistoryChatTitle(String model);

    void updateDeleteHistoryChatTitle(ChatHistoryDto chatHistoryDto);


    List<ChatHistoryContentVo> selectHistoryChatContent(ChatHistoryContentDto ChatHistoryContentDto);

}
