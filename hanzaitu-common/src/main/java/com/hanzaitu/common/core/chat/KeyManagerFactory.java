package com.hanzaitu.common.core.chat;

import com.hanzaitu.common.domain.ChatKeyEntity;
import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.chat.CircularBlockingQueue;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 轮询chatKey
 */
@Component
public class KeyManagerFactory {


    private Map<GptKeyTypeEnum, CircularBlockingQueue<ChatKeyEntity>> keyQueue = new ConcurrentHashMap<>();

    private synchronized ChatKeyEntity getKeyVal(CircularBlockingQueue<ChatKeyEntity> blockObj) {
        return blockObj.next();
    }

    public ChatKeyEntity getKey(GptKeyTypeEnum key) {
        if (!keyQueue.containsKey(key)) {
            return null;
        }
        return getKeyVal(keyQueue.get(key));
    }

    /**
     * 添加队列
     * @param chatKeyEntity
     */
    public void addQueue(ChatKeyEntity chatKeyEntity) {
        if (!StringUtils.isEmpty(chatKeyEntity.getChatKey()) && chatKeyEntity.getModel() != null) {
            if (keyQueue.containsKey(chatKeyEntity.getModel())) {
                keyQueue.get(chatKeyEntity.getModel()).add(chatKeyEntity);
            } else {
                CircularBlockingQueue<ChatKeyEntity> circularBlockingQueue = new CircularBlockingQueue<ChatKeyEntity>();
                circularBlockingQueue.add(chatKeyEntity);
                keyQueue.put(chatKeyEntity.getModel(), circularBlockingQueue);
            }
        }
    }

    /**
     * 删除key
     * @param chatKeyEntity
     */
    public void delQueue(ChatKeyEntity chatKeyEntity) {
        if (!StringUtils.isEmpty(chatKeyEntity.getChatKey()) && chatKeyEntity.getModel() != null &&
                keyQueue.containsKey(chatKeyEntity.getModel())) {
            keyQueue.get(chatKeyEntity.getModel()).remove(chatKeyEntity);
        }
    }

}
