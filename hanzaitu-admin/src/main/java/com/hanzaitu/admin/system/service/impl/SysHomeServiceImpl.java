package com.hanzaitu.admin.system.service.impl;

import com.hanzaitu.admin.system.domain.AiHomeBanner;
import com.hanzaitu.admin.system.domain.dto.AiHomeBannerDto;
import com.hanzaitu.admin.system.mapper.AiHomeBannerMapper;
import com.hanzaitu.admin.system.service.ISysHomeService;
import com.hanzaitu.common.config.AppProperties;
import com.hanzaitu.common.config.HanZaiTuConfig;
import com.hanzaitu.common.config.ServerConfig;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.utils.bean.BeanUtils;
import com.hanzaitu.common.utils.file.FileUploadUtils;
import com.hanzaitu.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SysHomeServiceImpl implements ISysHomeService {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private AiHomeBannerMapper aiChatExpiredConfigMapper;


    @Autowired
    private AppProperties appProperties;

    @Override
    public AjaxResult save(AiHomeBannerDto aiHomeBannerDto) {
        // 上传文件路径
        String filePath = FileUtils.getAbsolutePath(appProperties.getStaticDir())+ File.separator+"banner";
        String fileName = "";
        // 上传并返回新文件名称
        try {
            fileName = FileUploadUtils.upload(filePath, aiHomeBannerDto.getFile(),false);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error();
        }
        AiHomeBanner aiHomeBanner = new AiHomeBanner();
        BeanUtils.copyBeanProp(aiHomeBanner,aiHomeBannerDto);
        aiHomeBanner.setImgUrl(fileName);
        aiChatExpiredConfigMapper.saveOrUpdateById(aiHomeBanner);
        return AjaxResult.success();
    }

}
