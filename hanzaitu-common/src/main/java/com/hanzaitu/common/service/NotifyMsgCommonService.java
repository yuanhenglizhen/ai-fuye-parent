package com.hanzaitu.common.service;

import org.springframework.scheduling.annotation.Async;

public interface NotifyMsgCommonService {

    @Async
    void publishUserMessage(Long userId, String mdMsg);
}
