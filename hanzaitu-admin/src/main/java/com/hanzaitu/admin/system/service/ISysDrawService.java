package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.dto.DrawCheckDto;
import com.hanzaitu.common.core.domain.AjaxResult;
import org.springframework.transaction.annotation.Transactional;

public interface ISysDrawService {
    @Transactional
    AjaxResult checkDraw(DrawCheckDto drawCheckDto);
}
