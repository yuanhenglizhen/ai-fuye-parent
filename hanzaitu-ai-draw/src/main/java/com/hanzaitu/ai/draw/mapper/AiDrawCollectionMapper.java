package com.hanzaitu.ai.draw.mapper;

import com.hanzaitu.ai.draw.domain.entity.AiDrawCollectEntity;
import com.hanzaitu.common.core.HztBaseMapper;
import com.hanzaitu.common.core.mybatis.MybatisPage;
import com.hanzaitu.common.domain.AiDrawQuadratEntity;
import org.apache.ibatis.annotations.Param;

public interface AiDrawCollectionMapper extends HztBaseMapper<AiDrawCollectEntity > {


    MybatisPage<AiDrawCollectEntity> selectDrawCollectList(MybatisPage convert, @Param("userId") Long userId);

}
