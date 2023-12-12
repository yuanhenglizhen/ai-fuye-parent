package com.hanzaitu.admin.system.service.impl;

import com.hanzaitu.admin.system.domain.AiGptModel;
import com.hanzaitu.admin.system.mapper.SysAiGptModelMapper;
import com.hanzaitu.admin.system.service.ISysAiGptModelService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SysAiGptModelServiceImpl implements ISysAiGptModelService {

    @Autowired
    private SysAiGptModelMapper sysAiGptModelMapper;


    @Override
    public List<AiGptModel> getAiGptModelList() {
        return sysAiGptModelMapper.selectAll();
    }


    @Override
    public int saveAiGptModel(List<AiGptModel> aiGptModelList) {
        return sysAiGptModelMapper.saveOrUpdateBatch(aiGptModelList) ? 1 : 0;
    }

}
