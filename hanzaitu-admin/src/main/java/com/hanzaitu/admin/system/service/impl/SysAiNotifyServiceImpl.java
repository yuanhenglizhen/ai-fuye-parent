package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hanzaitu.admin.system.domain.AiSysNotify;
import com.hanzaitu.admin.system.domain.AiUserNotify;
import com.hanzaitu.admin.system.mapper.SysAiSysNotifyMapper;
import com.hanzaitu.admin.system.mapper.SysAiUserNotifyMapper;
import com.hanzaitu.admin.system.service.ISysAiNotifyService;

import com.hanzaitu.common.enums.NotifyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAiNotifyServiceImpl implements ISysAiNotifyService {

    @Autowired
    private SysAiSysNotifyMapper sysAiSysNotifyMapper;


    @Autowired
    private SysAiUserNotifyMapper sysAiUserNotifyMapper;

    /**
     *查询全局通知
     * @return
     */
    @Override
    public AiSysNotify getSysNotify() {
        return sysAiSysNotifyMapper.selectOne(new QueryWrapper<>());
    }

    /**
     *更新全局通知
     * @return
     */
    @Override
    public int saveSysNotify(AiSysNotify aiSysNotify) {
        return sysAiSysNotifyMapper.saveOrUpdateById(aiSysNotify);
    }

    /**
     * 获取用户通知
     * @return
     */
    @Override
    public List<AiUserNotify> getUserNotify() {
        QueryWrapper queryWrapper = new QueryWrapper<AiUserNotify>();
        queryWrapper.eq("type", NotifyType.GLOBAL);
        queryWrapper.eq("del_flag",0);
        return sysAiUserNotifyMapper.selectList(queryWrapper);
    }

    /**
     * 更新或新增用户通知
     * @return
     */
    @Override
    public int saveUserNotify(AiUserNotify aiUserNotify) {
        aiUserNotify.setType(NotifyType.GLOBAL);
        return sysAiUserNotifyMapper.saveOrUpdateById(aiUserNotify);
    }

    /**
     * 删除
     * @return
     */
    @Override
    public int deleteUserNotify(Integer ids) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",ids);
        updateWrapper.set("del_flag",1);
        return sysAiUserNotifyMapper.update(null,updateWrapper);
    }

}
