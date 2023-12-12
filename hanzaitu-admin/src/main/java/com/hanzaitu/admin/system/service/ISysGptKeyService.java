package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.GptKey;
import com.hanzaitu.common.domain.ChatKeyEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ISysGptKeyService {

    List<GptKey> selectList();

    int insertGptKey(GptKey gptKey);

    @Async
    void pushRedisAsync(ChatKeyEntity chatKeyEntity);

    int updateGptKey(GptKey gptKey);


    int deleteGptKey(Integer gptKeyId);
}
