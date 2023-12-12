package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hanzaitu.admin.system.domain.DiscordAuthToken;
import com.hanzaitu.admin.system.mapper.SysAiDrawKeyMapper;
import com.hanzaitu.admin.system.service.ISysAiDrawKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAiDrawKeyServiceImpl implements ISysAiDrawKeyService {

    @Autowired
    private SysAiDrawKeyMapper sysGptKeyMapper;

    /**
     * 查询列表
     * @return
     */
    @Override
    public List<DiscordAuthToken> selectList() {
        QueryWrapper queryWrapper = new QueryWrapper<DiscordAuthToken>();
        queryWrapper.eq("del_flag",'0');
        return sysGptKeyMapper.selectList(queryWrapper);
    }


    /**
     * 更新或插入
     *
     */
    @Override
    public int save(DiscordAuthToken discordAuthToken) {
        return sysGptKeyMapper.saveOrUpdateById(discordAuthToken);
    }

    /**
     * 删除key
     * @param id
     * @return
     */
    @Override
    public int del(Integer id) {
        UpdateWrapper<DiscordAuthToken> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("token_id",id).set("del_flag", '1');
        return sysGptKeyMapper.update(null, updateWrapper);
    }

}
