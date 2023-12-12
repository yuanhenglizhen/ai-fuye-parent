package com.hanzaitu.admin.system.service;


import com.hanzaitu.admin.system.domain.AiSysNotify;
import com.hanzaitu.admin.system.domain.AiUserNotify;

import java.util.List;

public interface ISysAiNotifyService {


    AiSysNotify getSysNotify();

    int saveSysNotify(AiSysNotify aiSysNotify);

    List<AiUserNotify> getUserNotify();

    int saveUserNotify(AiUserNotify aiUserNotify);

    int deleteUserNotify(Integer ids);
}
