package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.admin.system.domain.AiAppRelease;
import com.hanzaitu.admin.system.mapper.SysAiAppReleaseMapper;
import com.hanzaitu.admin.system.service.ISysAiAppReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAiAppReleaseServiceImpl implements ISysAiAppReleaseService {

    @Autowired
    private SysAiAppReleaseMapper sysAiAppReleaseMapper;


    @Override
    public List<AiAppRelease> getReleaseList() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag",'0');
        queryWrapper.orderBy(true,false,"version");
        return sysAiAppReleaseMapper.selectList(queryWrapper);
    }


    @Override
    public int saveAiAppRelease(AiAppRelease aiAppRelease) {
        return sysAiAppReleaseMapper.saveOrUpdateById(aiAppRelease);
    }

    /**
     * 删除id
     * @param ids
     * @return
     */
    @Override
    public int deleteAiAppRelease(Integer ids) {
        return sysAiAppReleaseMapper.deleteById(ids);
    }


}
