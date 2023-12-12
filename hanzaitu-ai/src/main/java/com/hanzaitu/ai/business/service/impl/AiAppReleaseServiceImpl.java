package com.hanzaitu.ai.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.business.domain.AiAppRelease;
import com.hanzaitu.ai.business.dto.GetVersionDto;
import com.hanzaitu.ai.business.mapper.AiAppReleaseMapper;
import com.hanzaitu.ai.business.service.AiAppReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiAppReleaseServiceImpl implements AiAppReleaseService {

    @Autowired
    private AiAppReleaseMapper aiAppReleaseMapper;

    /**
     * 获取版本列表
     * @param getVersionDto
     * @return
     */
    @Override
    public List<AiAppRelease> getVersionList(GetVersionDto getVersionDto) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("del_flag",'0');
        queryWrapper.eq("app_type",getVersionDto.getAppType());
        queryWrapper.gt("version",getVersionDto.getVersion());
        return aiAppReleaseMapper.selectList(queryWrapper);
    }

}
