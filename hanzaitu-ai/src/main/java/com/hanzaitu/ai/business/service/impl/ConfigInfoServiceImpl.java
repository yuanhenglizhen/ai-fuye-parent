package com.hanzaitu.ai.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.business.domain.AiHomeBanner;
import com.hanzaitu.ai.business.domain.HomeConfig;
import com.hanzaitu.ai.business.domain.SysConfigEntity;
import com.hanzaitu.ai.business.domain.SysConfigPayType;
import com.hanzaitu.ai.business.mapper.AiHomeBannerMapper;
import com.hanzaitu.ai.business.mapper.SysConfigInfoMapper;
import com.hanzaitu.ai.business.service.ConfigInfoService;
import com.hanzaitu.ai.business.vo.ConfigInfoVo;
import com.hanzaitu.ai.business.vo.HomeBannerVo;
import com.hanzaitu.ai.chat.domain.entity.AiChatExpiredConfigEntity;
import com.hanzaitu.ai.chat.service.ChatService;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.ServletUtils;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.bean.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class ConfigInfoServiceImpl implements ConfigInfoService {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SysConfigInfoMapper sysConfigInfoMapper;


    @Autowired
    private AiHomeBannerMapper aiHomeBannerMapper;


    @Autowired
    private UploadLoadConfig uploadLoadConfig;


    @Override
    public ConfigInfoVo getPayConfigInfo() {
        ConfigInfoVo configInfoVo = new ConfigInfoVo();

        //只查一次数据库即可
        List<SysConfigEntity> selectSysConfigList= sysConfigInfoMapper.selectSysConfigList(
                Arrays.asList("pay_way","home_config"));

        if (!selectSysConfigList.isEmpty()) {
            for (SysConfigEntity s : selectSysConfigList) {
                if (s.getConfigKey().equals("pay_way")) {
                    configInfoVo.setPayType(JacksonUtil.parseObject(s.getConfigValue(),SysConfigPayType.class));
                } else if (s.getConfigKey().equals("home_config")) {
                    HomeConfig homeConfig = JacksonUtil.parseObject(s.getConfigValue(),HomeConfig.class);
                    if (homeConfig != null) {
                        homeConfig.setLoginExhibit(putImgUrl(homeConfig.getLoginExhibit()));
                    }
                    configInfoVo.setHomeConfig(homeConfig);
                }
            }
        }

        //查询聊天记录过期配置
        AiChatExpiredConfigEntity aiChatExpiredConfigEntity = chatService.getChatExpiredConfig();
        configInfoVo.setExpiredConfig(aiChatExpiredConfigEntity);
        return configInfoVo;
    }

    /**
     * 获取轮播图
     * @return
     */
    @Override
    public AjaxResult getHomeBannerList(){

        List<AiHomeBanner> homeBannerVoList = new ArrayList<>();
        //查询轮播图
        List<AiHomeBanner> sysPayConfig = aiHomeBannerMapper.selectList(
                new QueryWrapper<AiHomeBanner>().eq("del_flag","0")
                        .orderBy(true,false,"sort"));
        if (sysPayConfig != null){
            for (AiHomeBanner v : sysPayConfig) {
                v.setImgUrl(putImgUrl(v.getImgUrl()));
                homeBannerVoList.add(v);
            }
        }
        return AjaxResult.success(homeBannerVoList);
    }


    private String putImgUrl(String imgPath) {
        if (StringUtils.isNotEmpty(imgPath)
                && (imgPath.startsWith("/") || imgPath.startsWith("\\"))) {
            String  domain = uploadLoadConfig.getQnyImageDomain();
            HttpServletRequest request =  ServletUtils.getRequest();
            String originHost = request.getHeader("Hzt-Origin");
            String oriHost = request.getHeader("Host");
            String localDomain = StringUtils.isEmpty(originHost) ? "http://"+oriHost : originHost;
            String resImg = "";
            if (StringUtils.isNotEmpty(localDomain) && imgPath.startsWith("/static")) {
                String localDomainStr = localDomain.endsWith("/") ? localDomain.substring(0,domain.length()-1) : localDomain;
                resImg = localDomainStr+"/api"+imgPath;
            }else if (StringUtils.isNotEmpty(domain)){
                String domainStr = domain.endsWith("/") ? domain.substring(0,domain.length()-1) : domain;
                resImg = domainStr + imgPath;
            }
            return resImg.equals("") ? imgPath : resImg;
        }
        return imgPath;
    }

}
