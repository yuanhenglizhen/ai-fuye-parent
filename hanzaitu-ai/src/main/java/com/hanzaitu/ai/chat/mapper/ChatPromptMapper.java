package com.hanzaitu.ai.chat.mapper;

import com.hanzaitu.ai.chat.domain.entity.AiCollectPromptEntity;
import com.hanzaitu.ai.chat.domain.entity.AiPromptCategoryEntity;
import com.hanzaitu.ai.chat.domain.entity.AiPromptEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatPromptMapper {

    List<AiCollectPromptEntity> selectCollectPrompt(@Param("userId") Integer userId);

    List<AiPromptCategoryEntity> selectPromptCategory();

    List<AiPromptEntity> selectPrompt(@Param("categoryId") Integer categoryId);

    AiCollectPromptEntity selectCollectPromptByMsgId(@Param("msgId") String msgId);


    int deleteCollectPrompt(@Param("msgId") String msgId);

    int insertCollectPrompt(AiCollectPromptEntity aiCollectPromptEntity);

}
