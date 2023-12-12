package com.hanzaitu.common.mapper;

import com.hanzaitu.common.core.HztBaseMapper;
import com.hanzaitu.common.core.mybatis.MybatisPage;
import com.hanzaitu.common.domain.FinanceUserWalletRecord;
import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.common.params.FinanceUserWalletRecordQueryParam;
import com.hanzaitu.common.vo.FinanceUserWalletRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户财产积分钱包记录表 Mapper 接口
 * </p>
 *
 * @author cr
 * @since 2023-06-04
 */
@org.apache.ibatis.annotations.Mapper
public interface FinanceUserWalletRecordMapper extends HztBaseMapper<FinanceUserWalletRecord> {
    /**
     * 获取用户钱包记录
     * @param customerUserId
     * @return
     */
    MybatisPage<FinanceUserWalletRecordVO> selectListByCustomerUserId(MybatisPage convert, @Param("param") FinanceUserWalletRecordQueryParam param);

}
