package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.AiDrawQuadratLabel;
import com.hanzaitu.common.page.HztPage;

import java.util.List;

public interface IAiDrawQuadratLabelService {
    HztPage<AiDrawQuadratLabel> selectList();

    void deleteOne(Long id);

    void updateLabel(AiDrawQuadratLabel label);

    void addLabel(AiDrawQuadratLabel label);
}
