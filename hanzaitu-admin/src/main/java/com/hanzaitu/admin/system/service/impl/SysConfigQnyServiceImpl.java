package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.admin.system.domain.AiChatExpiredConfig;
import com.hanzaitu.admin.system.domain.SysQnyConfig;
import com.hanzaitu.admin.system.mapper.AiChatExpiredConfigMapper;
import com.hanzaitu.admin.system.mapper.SysQnyConfigMapper;
import com.hanzaitu.admin.system.service.ISysConfigQnyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SysConfigQnyServiceImpl implements ISysConfigQnyService {

    @Autowired
    private SysQnyConfigMapper sysQnyConfigMapper;


    @Autowired
    private AiChatExpiredConfigMapper aiChatExpiredConfigMapper;

    @Override
    public SysQnyConfig getSysQnyConfig() {
        return sysQnyConfigMapper.selectOne(new QueryWrapper<>());
    }

    @Override
    public int updateSysQnyConfig(SysQnyConfig sysQnyConfig) {
        return sysQnyConfigMapper.saveOrUpdateById(sysQnyConfig);
    }


    /**
     * 获取聊天过期配置
     * @return
     */

    @Override
    public AiChatExpiredConfig getAiChatExpiredConfig() {
        return aiChatExpiredConfigMapper.selectOne(new QueryWrapper<>());
    }

    /**
     * 更新聊天配置
     * @param aiChatExpiredConfig
     * @return
     */
    @Override
    public int saveAiChatExpiredConfig(AiChatExpiredConfig aiChatExpiredConfig) {
        return aiChatExpiredConfigMapper.saveOrUpdateById(aiChatExpiredConfig);
    }
}
