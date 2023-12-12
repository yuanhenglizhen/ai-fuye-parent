package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hanzaitu.admin.system.domain.AiDrawQuadrat;
import com.hanzaitu.admin.system.domain.AiDrawQuadratCheck;
import com.hanzaitu.admin.system.domain.AiDrawQuadratLabel;
import com.hanzaitu.admin.system.mapper.AiSystemDrawQuadratCheckMapper;
import com.hanzaitu.admin.system.mapper.AiSystemDrawQuadratMapper;
import com.hanzaitu.admin.system.service.IAiDrawQuadratCheckService;
import com.hanzaitu.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiDrawQuadratCheckServiceImpl implements IAiDrawQuadratCheckService {

    @Autowired
    private AiSystemDrawQuadratCheckMapper aiDrawQuadratCheckMapper;


    @Autowired
    private AiSystemDrawQuadratMapper aiDrawQuadratMapper;

    @Override
    public List<AiDrawQuadratCheck> selectList() {
        return aiDrawQuadratCheckMapper.selectAll();
    }

    @Override
    public List<AiDrawQuadratCheck> selectNotPassList() {
        LambdaQueryWrapper<AiDrawQuadratCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiDrawQuadratCheck::getCheckStatus,"0");//根据审核状态0查询列表
        List<AiDrawQuadratCheck> checks = aiDrawQuadratCheckMapper.selectList(wrapper);
        return checks;
    }



    @Override
    public void shelf(AiDrawQuadratCheck check, String status) {
        String taskId = check.getTaskId();
        if (status.equals("on")){
            //上架
            LambdaQueryWrapper<AiDrawQuadrat> quadratWrapper = new LambdaQueryWrapper<>();
            quadratWrapper.eq(AiDrawQuadrat::getTaskId,taskId);
            AiDrawQuadrat aiDrawQuadrat = aiDrawQuadratMapper.selectOne(quadratWrapper);
            aiDrawQuadrat.setStatus("0"); //将状态设置为0，上架状态
            aiDrawQuadratMapper.updateById(aiDrawQuadrat);
        }else if (status.equals("off")){
            LambdaQueryWrapper<AiDrawQuadrat> quadratWrapper = new LambdaQueryWrapper<>();
            quadratWrapper.eq(AiDrawQuadrat::getTaskId,taskId);
            AiDrawQuadrat aiDrawQuadrat = aiDrawQuadratMapper.selectOne(quadratWrapper);
            aiDrawQuadrat.setStatus("1"); //将状态设置为1，下架状态
            aiDrawQuadratMapper.updateById(aiDrawQuadrat);
        }
    }
}
