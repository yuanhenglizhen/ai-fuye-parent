package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.AiGptModel;

import java.util.List;

public interface ISysAiGptModelService {
    List<AiGptModel> getAiGptModelList();

    int saveAiGptModel(List<AiGptModel> aiGptModelList);
}
