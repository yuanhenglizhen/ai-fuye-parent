package com.hanzaitu.ai.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.business.domain.AiSysNotify;
import com.hanzaitu.common.core.domain.entity.AiUserNotify;
import com.hanzaitu.ai.business.mapper.AiSysNotifyMapper;
import com.hanzaitu.ai.business.service.NotifyMsgService;
import com.hanzaitu.ai.business.vo.NotifyVo;

import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.enums.NotifyType;
import com.hanzaitu.common.mapper.AiUserNotifyMapper;
import com.hanzaitu.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NotifyMsgServiceImpl implements NotifyMsgService {

    @Autowired
    private AiUserNotifyMapper aiUserNotifyMapper;

    @Autowired
    private AiSysNotifyMapper aiSysNotifyMapper;

    @Override
    public List<NotifyVo> selectUserNotifyList() {
        List<NotifyVo> userNotifyVoList = new ArrayList<>();
        Long userId = UserThreadLocal.get().getUserId();
        String dateTime = DateUtils.getSpecifyDate(-30);
        QueryWrapper<AiUserNotify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",0);
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("type",NotifyType.USER);
        queryWrapper.gt("create_time",dateTime);
        queryWrapper.orderBy(true,false,"create_time");
        List<AiUserNotify> aiUserNotifies = aiUserNotifyMapper.selectList(queryWrapper);
        if (!aiUserNotifies.isEmpty()) {
            for (AiUserNotify a:aiUserNotifies) {
                NotifyVo userNotifyVo = new NotifyVo();
                userNotifyVo.setId(a.getId());
                userNotifyVo.setContent(a.getContent());
                userNotifyVo.setTitle(a.getTitle());
                userNotifyVo.setType(a.getType());
                userNotifyVo.setCreateTime(a.getCreateTime());
                userNotifyVoList.add(userNotifyVo);
            }
        }
        return userNotifyVoList;
    }

    /**
     * 系统弹框
     * @return
     */
    @Override
    public NotifyVo selectSysNotifyList() {
        NotifyVo userNotifyVoRes = new NotifyVo();
        QueryWrapper<AiSysNotify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",0);
        queryWrapper.le("start_time",DateUtils.getTime());
        queryWrapper.ge("end_time",DateUtils.getTime());
        queryWrapper.orderBy(true,false,"create_time");
        AiSysNotify aiSysNotify = aiSysNotifyMapper.selectOne(queryWrapper);

        if (aiSysNotify != null) {
            userNotifyVoRes.setId(aiSysNotify.getId());
            userNotifyVoRes.setTitle(aiSysNotify.getTitle());
            userNotifyVoRes.setType(NotifyType.GLOBAL);
            userNotifyVoRes.setContent(aiSysNotify.getContent());
            userNotifyVoRes.setCreateTime(aiSysNotify.getCreateTime());
        }

        return userNotifyVoRes;
    }


    /**
     * 系统弹框
     * @return
     */
    @Override
    public List<NotifyVo> selectHomeNotifyList() {
        List<NotifyVo> userNotifyVoList = new ArrayList<>();
        //String dateTime = DateUtils.getSpecifyDate(-30);
        QueryWrapper<AiUserNotify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",0);
        queryWrapper.eq("user_id",0);
        queryWrapper.eq("type",NotifyType.GLOBAL);
        queryWrapper.le("start_time",DateUtils.getTime());
        queryWrapper.ge("end_time",DateUtils.getTime());
        //queryWrapper.gt("create_time",dateTime);
        queryWrapper.orderBy(true,false,"create_time");
        List<AiUserNotify> aiUserNotifies = aiUserNotifyMapper.selectList(queryWrapper);
        if (!aiUserNotifies.isEmpty()) {
            for (AiUserNotify a:aiUserNotifies) {

                NotifyVo userNotifyVo = new NotifyVo();
                userNotifyVo.setId(a.getId());
                userNotifyVo.setContent(a.getContent());
                userNotifyVo.setTitle(a.getTitle());
                userNotifyVo.setType(a.getType());
                userNotifyVo.setCreateTime(a.getCreateTime());
                userNotifyVoList.add(userNotifyVo);
            }
        }
        return userNotifyVoList;
    }

}
