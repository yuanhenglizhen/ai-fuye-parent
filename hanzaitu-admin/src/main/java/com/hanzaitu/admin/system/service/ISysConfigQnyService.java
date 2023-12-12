package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.AiChatExpiredConfig;
import com.hanzaitu.admin.system.domain.SysQnyConfig;

public interface ISysConfigQnyService {


    SysQnyConfig getSysQnyConfig();

    int updateSysQnyConfig(SysQnyConfig sysQnyConfig);

    AiChatExpiredConfig getAiChatExpiredConfig();

    int saveAiChatExpiredConfig(AiChatExpiredConfig aiChatExpiredConfig);
}
