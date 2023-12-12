package com.hanzaitu.common.service.impl;

import com.hanzaitu.common.core.domain.entity.AiUserNotify;
import com.hanzaitu.common.enums.NotifyType;
import com.hanzaitu.common.mapper.AiUserNotifyMapper;
import com.hanzaitu.common.service.NotifyMsgCommonService;
import com.hanzaitu.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class NotifyMsgCommonServiceImpl implements NotifyMsgCommonService {

    @Autowired
    private AiUserNotifyMapper aiUserNotifyMapper;

    /**
     * 推送用户消息通知
     * @param userId
     * @param mdMsg
     */
    @Async
    @Override
    public void publishUserMessage(Long userId, String mdMsg) {
        mdMsg = mdMsg.replace("$date", DateUtils.getTime());
        AiUserNotify aiSysNotify = new AiUserNotify();
        aiSysNotify.setContent(mdMsg);
        aiSysNotify.setTitle("系统通知");
        aiSysNotify.setUserId(userId);
        aiSysNotify.setType(NotifyType.USER);
        aiUserNotifyMapper.insert(aiSysNotify);
    }
}
