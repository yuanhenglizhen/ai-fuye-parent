package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.AiDrawQuadrat;

import java.util.List;

public interface IAiDrawQuadratService {
    List<AiDrawQuadrat> selectList(Integer labelId);

}
