package com.hanzaitu.ai.draw.mapper;

import com.hanzaitu.ai.draw.domain.entity.AiDrawPromptEntity;
import com.hanzaitu.ai.draw.domain.entity.DiscordAuthTokenEntity;

import java.util.List;

public interface AiDrawKeyMapper {

    List<DiscordAuthTokenEntity> selectDrawKeyList();

}
