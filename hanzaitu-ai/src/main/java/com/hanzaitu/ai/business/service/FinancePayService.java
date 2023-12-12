package com.hanzaitu.ai.business.service;

import com.hanzaitu.ai.business.vo.WxPayVO;
import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.ai.business.param.AliPayCreateOrderParam;
import com.hanzaitu.ai.business.param.WxPayCallbackParam;
import com.hanzaitu.ai.business.param.WxPayCreateOrderParam;
import com.hanzaitu.ai.business.param.YPayCreateOrderParam;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.FinanceCipherCodeParam;
import com.hanzaitu.common.params.FinanceUserWalletRecordQueryParam;
import com.hanzaitu.common.vo.FinanceCipherCodeVO;
import com.hanzaitu.common.vo.FinanceUserWalletRecordVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 微信支付记录 服务类
 * </p>
 *
 * @author cr
 * @since 2023-06-03
 */
public interface FinancePayService {


    /**
     * 创建微信支付订单
     * @param param
     * @return
     */
    WxPayVO createWxPayOrder(WxPayCreateOrderParam param);

    /**
     * 微信回调方法
     * @param param
     */
    void wxCallback(WxPayCallbackParam param);



    /**
     * 创建支付宝支付订单
     * @param param
     * @return
     */
    String createAliPayOrder(AliPayCreateOrderParam param);


    /**
     * 创建支付宝app支付订单
     * @param param
     * @return
     */
    String createAliPayAppOrder(AliPayCreateOrderParam param);

    /**
     * 支付宝回调方法
     * @param params
     */
    void aliPayCallback(Map<String,String> params);

    /**
     * 创建卡密支付订单
     * @param param
     */
    void createCipherCodePayOrder(FinanceCipherCodeParam param);


    /**
     * 获取卡密信息
     * @param code
     */
    FinanceCipherCodeVO getCipherCode(String code);

    /**
     * 获取用户钱包记录
     * @return
     */
    HztPage<FinanceUserWalletRecordVO> listUserWalletRecord(FinanceUserWalletRecordQueryParam param);


    /**
     * 创建源支付支付订单
     * @param param
     * @return
     */
    String createYPayOrder(YPayCreateOrderParam param, boolean isApp);

    /**
     * 支付宝回调方法
     * @param params
     */
    String yPayCallback(Map<String,String> params);

    /**
     * 微信结果查询
     * @param resultNo
     */
    Boolean getWxPayResult(String resultNo);
}
