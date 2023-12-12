package com.hanzaitu.admin.system.service.impl;

import com.hanzaitu.admin.system.domain.GptKey;
import com.hanzaitu.admin.system.mapper.SysGptKeyMapper;
import com.hanzaitu.admin.system.service.ISysGptKeyService;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.domain.ChatKeyEntity;
import com.hanzaitu.common.domain.ChatKeyRedisList;
import com.hanzaitu.common.enums.KeyManagerRedisOperationType;
import com.hanzaitu.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SysGptKeyServiceImpl implements ISysGptKeyService {

    @Autowired
    private SysGptKeyMapper sysGptKeyMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public List<GptKey> selectList() {
        return sysGptKeyMapper.selectGptKeyList();
    }

    /**
     * 插入gpt key
     * @param gptKey
     * @return
     */
    public int insertGptKey(GptKey gptKey) {
        if (findOne(gptKey.getChatKey()) != null) {
            throw new ServiceException("key已存在");
        }
        return sysGptKeyMapper.insertGptKey(gptKey);
    }

    @Async
    @Override
    public void pushRedisAsync(ChatKeyEntity chatKeyEntity) {
        try {
            ChatKeyRedisList chatKeyRedisList = new ChatKeyRedisList();
            chatKeyRedisList.setKeyManagerRedisOperationType(KeyManagerRedisOperationType.ADD);
            chatKeyRedisList.setData(chatKeyEntity);
            redisCache.bpush(CacheConstants.CHAT_QUEUE_KEY, chatKeyRedisList);
        }catch (Exception e) {
            log.error(e.toString());
        }
    }

    public int updateGptKey(GptKey gptKey) {
        return sysGptKeyMapper.updateGptKey(gptKey);
    }

    public int deleteGptKey(Integer gptKeyId) {
        return sysGptKeyMapper.deleteGptKey(gptKeyId);
    }

    public GptKey findOne(String gptKey) {
        return sysGptKeyMapper.findOneGptKey(gptKey);
    }

}
