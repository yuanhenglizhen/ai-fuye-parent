package com.hanzaitu.common.service;

import com.hanzaitu.common.domain.FinanceUserWallet;
import com.hanzaitu.common.enums.RecordTypeEnum;
import com.hanzaitu.common.params.WalletPointParam;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户财产积分记录表 服务类
 * </p>
 *
 * @author cr
 * @since 2023-06-03
 */
public interface FinanceUserWalletService  {


    /**
     * 更新钱包积分
     */
    void updateWalletPoints(WalletPointParam param);

    /**
     * 初始化用户钱包
     * @param customerUserId
     */
    void initUserWallet(Long customerUserId, Integer initPoints);

    /**
     * 消耗积分
     * @param typeEnum
     */
    WalletPointParam expendGptPoints(RecordTypeEnum typeEnum);

    @Transactional(rollbackFor = Exception.class)
    void returnGptPoints(RecordTypeEnum typeEnum, Integer points, Long userId);
}