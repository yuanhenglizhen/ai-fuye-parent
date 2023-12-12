package com.hanzaitu.ai.draw.service;

import com.hanzaitu.ai.draw.domain.dto.DrawQuadratDto;
import com.hanzaitu.ai.draw.domain.dto.DrawQuadratPraiseDto;
import com.hanzaitu.ai.draw.domain.entity.AiDrawCollectEntity;
import com.hanzaitu.ai.draw.domain.vo.AiDrawRraiseListVo;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.domain.AiDrawQuadratEntity;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.PageParam;
import org.springframework.transaction.annotation.Transactional;

public interface DrawSquareService {
    AjaxResult selectLabel();

    HztPage<AiDrawQuadratEntity> selectList(DrawQuadratDto drawQuadratDto);

    HztPage<AiDrawCollectEntity> collectDrawList(PageParam pageParam);

    Boolean drawCollectStatus(String taskId);

    HztPage<AiDrawRraiseListVo> aiDrawRraiseList(PageParam pageParam);

    @Transactional
    AjaxResult praise(DrawQuadratPraiseDto drawQuadratPraiseDto);
}
