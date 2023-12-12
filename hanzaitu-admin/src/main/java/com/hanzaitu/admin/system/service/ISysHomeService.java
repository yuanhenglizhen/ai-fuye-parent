package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.dto.AiHomeBannerDto;
import com.hanzaitu.common.core.domain.AjaxResult;

public interface ISysHomeService {

    AjaxResult save(AiHomeBannerDto aiHomeBannerDto);
}
