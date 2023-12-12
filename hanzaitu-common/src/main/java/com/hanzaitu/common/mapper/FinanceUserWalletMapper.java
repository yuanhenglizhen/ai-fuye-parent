package com.hanzaitu.common.mapper;

import com.hanzaitu.common.core.HztBaseMapper;
import com.hanzaitu.common.domain.FinanceUserWallet;
import com.hanzaitu.common.params.WalletPointParam;

/**
 * <p>
 * 用户财产积分记录表 Mapper 接口
 * </p>
 *
 * @author cr
 * @since 2023-06-03
 */
@org.apache.ibatis.annotations.Mapper
public interface FinanceUserWalletMapper extends HztBaseMapper<FinanceUserWallet> {


    /**
     * 积分更新
     */
    void updatePoints(WalletPointParam param);
}
