package com.hanzaitu.ai.business.service;

import com.hanzaitu.ai.business.vo.NotifyVo;


import java.util.List;

public interface NotifyMsgService {

    List<NotifyVo> selectUserNotifyList();


    NotifyVo selectSysNotifyList();

    List<NotifyVo> selectHomeNotifyList();

    /*void publishInviteFriendMessage(Long userId);

    void publishRechargeMessage(Long userId, Long score);*/

    //void publishMessage(Long userId, String mdMsg);
}
