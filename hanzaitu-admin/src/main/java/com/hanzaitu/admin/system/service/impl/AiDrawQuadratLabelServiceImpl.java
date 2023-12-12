package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.admin.system.domain.AiDrawQuadratLabel;
import com.hanzaitu.admin.system.mapper.AiDrawQuadratLabelMapper;
import com.hanzaitu.admin.system.service.IAiDrawQuadratLabelService;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiDrawQuadratLabelServiceImpl implements IAiDrawQuadratLabelService {

    @Autowired
    private AiDrawQuadratLabelMapper labelMapper;

    @Override
    public HztPage<AiDrawQuadratLabel> selectList() {
        List<AiDrawQuadratLabel> labelList = labelMapper.selectAll();
        PageParam pageParam = new PageParam();
        HztPage<AiDrawQuadratLabel> page = labelMapper.selectPage(pageParam, null);
        return page;
    }

    @Override
    public void deleteOne(Long id) {
        labelMapper.deleteById(id);
    }

    @Override
    public void updateLabel(AiDrawQuadratLabel label) {
        labelMapper.updateById(label);
    }

    @Override
    public void addLabel(AiDrawQuadratLabel label) {
        labelMapper.insert(label);
    }
}
