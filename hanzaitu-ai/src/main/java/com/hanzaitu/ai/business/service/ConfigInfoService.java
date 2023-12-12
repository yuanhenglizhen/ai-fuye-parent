package com.hanzaitu.ai.business.service;

import com.hanzaitu.ai.business.vo.ConfigInfoVo;
import com.hanzaitu.common.core.domain.AjaxResult;

public interface ConfigInfoService {
    ConfigInfoVo getPayConfigInfo();

    AjaxResult getHomeBannerList();
}
