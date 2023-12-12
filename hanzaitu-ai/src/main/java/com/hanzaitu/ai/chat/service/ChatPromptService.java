package com.hanzaitu.ai.chat.service;

import com.hanzaitu.ai.chat.domain.dto.AddChatPromptDto;
import com.hanzaitu.ai.chat.domain.dto.ChatCollectPromptDto;
import com.hanzaitu.ai.chat.domain.entity.AiPromptCustomEntity;
import com.hanzaitu.ai.chat.domain.vo.ChatCollectPromptListVo;
import com.hanzaitu.ai.chat.domain.vo.ChatPromptListVo;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.PageParam;

import java.util.List;

public interface ChatPromptService {

    List<ChatPromptListVo> selectPromptList();

    List<ChatCollectPromptListVo> selectCollectPromptList();

    AjaxResult addCollectPrompt(ChatCollectPromptDto chatCollectPromptDto);

    AjaxResult addCustomPrompt(AddChatPromptDto addChatPromptDto);

    HztPage<AiPromptCustomEntity> selectAiPromptCustomList(PageParam pageParam);

    AjaxResult deleteAiPromptCustom(Long id);
}
