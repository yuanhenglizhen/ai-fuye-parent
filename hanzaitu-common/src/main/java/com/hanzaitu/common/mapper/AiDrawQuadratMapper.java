package com.hanzaitu.common.mapper;


import com.hanzaitu.common.core.HztBaseMapper;
import com.hanzaitu.common.core.mybatis.MybatisPage;
import com.hanzaitu.common.domain.AiDrawQuadratEntity;
import com.hanzaitu.common.enums.draw.DrawQuadratSortTypeEnum;
import com.hanzaitu.common.vo.FinanceUserWalletRecordVO;
import org.apache.ibatis.annotations.Param;

@org.apache.ibatis.annotations.Mapper
public interface AiDrawQuadratMapper  extends HztBaseMapper<AiDrawQuadratEntity> {


    MybatisPage<AiDrawQuadratEntity> selectDrawQuadratList(MybatisPage convert, @Param("labelId") Integer labelId,
                                                           @Param("sortType") String sortType);
}
