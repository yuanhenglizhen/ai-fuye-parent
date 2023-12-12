package com.hanzaitu.ai.chat.mapper;

import com.hanzaitu.ai.chat.domain.entity.AiMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

public interface AiMemberMapper {

    AiMemberEntity selectAiMember(@Param("userId") Integer userId);
}
