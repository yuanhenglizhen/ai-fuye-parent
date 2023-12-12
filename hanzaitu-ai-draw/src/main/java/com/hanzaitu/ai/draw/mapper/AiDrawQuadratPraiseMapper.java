package com.hanzaitu.ai.draw.mapper;

import com.hanzaitu.ai.draw.domain.entity.AiDrawQuadratPraiseEntity;
import com.hanzaitu.ai.draw.domain.vo.AiDrawRraiseListVo;
import com.hanzaitu.common.core.HztBaseMapper;
import com.hanzaitu.common.core.mybatis.MybatisPage;
import org.apache.ibatis.annotations.Param;


public interface AiDrawQuadratPraiseMapper extends HztBaseMapper<AiDrawQuadratPraiseEntity > {

    /**
     * 查询点赞收藏列表
     * @param convert
     * @param userId
     * @return
     */
    MybatisPage<AiDrawRraiseListVo> selectDrawRraiseList(MybatisPage convert, @Param("userId") Long userId);

}
