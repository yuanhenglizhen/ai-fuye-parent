package com.hanzaitu.ai.draw.mapper;

import com.hanzaitu.ai.draw.domain.entity.AiDrawPromptEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiDrawPromptMapper {


    List<AiDrawPromptEntity> selectDrawPromptCategory();


    List<AiDrawPromptEntity> selectDrawPromptAll();


    List<AiDrawPromptEntity> selectDrawPromptByCategory(@Param("categoryParam") String categoryParam);
}
