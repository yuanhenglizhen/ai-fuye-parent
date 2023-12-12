package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hanzaitu.admin.system.domain.AiDrawQuadrat;
import com.hanzaitu.admin.system.domain.AiDrawQuadratLabelCheck;
import com.hanzaitu.admin.system.mapper.AiDrawQuadratLabelCheckMapper;
import com.hanzaitu.admin.system.mapper.AiSystemDrawQuadratMapper;
import com.hanzaitu.admin.system.service.IAiDrawQuadratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiDrawQuadratServiceImpl implements IAiDrawQuadratService {

    @Autowired
    private AiSystemDrawQuadratMapper aiDrawQuadratMapper;

    @Autowired
    private AiDrawQuadratLabelCheckMapper aiDrawQuadratLabelCheckMapper;

    @Override
    public List<AiDrawQuadrat> selectList(Integer labelId) {
        if (labelId ==null){ //如果没带分类id，则表示查全部
        List<AiDrawQuadrat> quadrats = aiDrawQuadratMapper.selectAll();
            return quadrats;
        } else {//否则就是根据分类查询
            LambdaQueryWrapper<AiDrawQuadratLabelCheck> quadratLabelCheckWrapper = new LambdaQueryWrapper<>();
            quadratLabelCheckWrapper.eq(AiDrawQuadratLabelCheck::getLabelId,labelId);
            List<AiDrawQuadratLabelCheck> labelCheckList = aiDrawQuadratLabelCheckMapper.selectList(quadratLabelCheckWrapper);//获取关联表中该分类下所有对应的图
            List<AiDrawQuadrat> list = new ArrayList<>();
            for (AiDrawQuadratLabelCheck aiDrawQuadratLabelCheck : labelCheckList) {
                String taskId = aiDrawQuadratLabelCheck.getTaskId();
                LambdaQueryWrapper<AiDrawQuadrat> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AiDrawQuadrat::getTaskId,taskId);
                AiDrawQuadrat quadrat = aiDrawQuadratMapper.selectOne(wrapper);//根据图id获取单个图
                list.add(quadrat);//添加到列表中返回
            }
            return list;
        }
    }
}
