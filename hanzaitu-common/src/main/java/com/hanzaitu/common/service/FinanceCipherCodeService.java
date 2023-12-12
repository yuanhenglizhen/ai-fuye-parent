package com.hanzaitu.common.service;

import com.hanzaitu.common.domain.FinanceCipherCode;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.FinanceCipherCodeParam;
import com.hanzaitu.common.vo.FinanceCipherCodeVO;

import java.util.List;

/**
 * <p>
 * 卡密表 服务类
 * </p>
 *
 * @author cr
 * @since 2023-06-11
 */
public interface FinanceCipherCodeService  {

    /**
     * 生成卡密code
     * @param count
     * @return
     */
    List<String> generateCipherCode(Integer count);

    /**
     * 新增卡密
     * @param cipherCodes
     * @return
     */
    void insertBatch(List<FinanceCipherCode> cipherCodes);

    /**
     * 查看卡密集合
     * @param cipherCode
     * @return
     */
    HztPage<FinanceCipherCodeVO> listCipherCode(FinanceCipherCodeParam param);

    /**
     * 获取卡密
     * @param code
     * @return
     */
    FinanceCipherCode getCipherCodeByCode(String code);
}