package com.hanzaitu.admin.system.service;

import com.hanzaitu.admin.system.domain.AiDrawQuadratCheck;

import java.util.List;

public interface IAiDrawQuadratCheckService {
    List<AiDrawQuadratCheck> selectList();

    //还未审核通过的图列表
    List<AiDrawQuadratCheck> selectNotPassList();



    //上下架
    void shelf(AiDrawQuadratCheck check, String status);
}
