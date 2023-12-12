package com.hanzaitu.common.domain;

import com.hanzaitu.common.enums.KeyManagerRedisOperationType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatKeyRedisList implements Serializable {

    /**
     * 操作类型
     */
    private KeyManagerRedisOperationType keyManagerRedisOperationType;

    /**
     * 传输数据
     */
    private ChatKeyEntity data;

}
