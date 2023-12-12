package com.hanzaitu.common.service;


import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.FinanceUserWalletRecordQueryParam;
import com.hanzaitu.common.params.FinanceUserWalletRecordSaveParam;
import com.hanzaitu.common.vo.FinanceUserWalletRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户财产积分钱包记录表 服务类
 * </p>
 *
 * @author cr
 * @since 2023-06-04
 */
@Mapper
public interface FinanceUserWalletRecordService  {

    /**
     * 保存钱包积分记录
     * @param param
     */
    void saveWalletRecord(FinanceUserWalletRecordSaveParam param);

    /**
     * 获取用户钱包记录
     * @return
     */
    HztPage<FinanceUserWalletRecordVO> listUserWalletRecord(FinanceUserWalletRecordQueryParam param);
}