package com.hanzaitu.ai.business.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.hanzaitu.ai.business.domain.FinanceAliPayRecord;
import com.hanzaitu.ai.business.domain.FinanceWxPayRecord;
import com.hanzaitu.common.enums.UserNotifyEnum;
import com.hanzaitu.ai.business.vo.WxPayVO;
import com.hanzaitu.common.domain.FinanceYPayRecord;
import com.hanzaitu.ai.business.enums.AliPayTradeStatusEnum;
import com.hanzaitu.ai.business.enums.HandleStatusEnum;
import com.hanzaitu.ai.business.enums.WxTradeStateEnum;
import com.hanzaitu.ai.business.mapper.FinanceAliPayRecordMapper;
import com.hanzaitu.ai.business.mapper.FinanceWxPayRecordMapper;
import com.hanzaitu.ai.business.param.*;
import com.hanzaitu.ai.business.service.FinancePayService;
import com.hanzaitu.ai.util.AliPayUtils;
import com.hanzaitu.ai.util.WxPayUtils;
import com.hanzaitu.ai.util.YPayUtil;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.FinanceCipherCode;
import com.hanzaitu.common.enums.CipherStatus;
import com.hanzaitu.common.enums.RecordTypeEnum;
import com.hanzaitu.common.mapper.FinanceCipherCodeMapper;
import com.hanzaitu.common.mapper.FinanceYPayRecordMapper;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.FinanceCipherCodeParam;
import com.hanzaitu.common.params.FinanceUserWalletRecordQueryParam;
import com.hanzaitu.common.params.FinanceUserWalletRecordSaveParam;
import com.hanzaitu.common.params.WalletPointParam;
import com.hanzaitu.common.service.*;
import com.hanzaitu.common.utils.RandomNumUtils;
import com.hanzaitu.common.utils.bean.BeanUtils;
import com.hanzaitu.common.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 微信支付记录 服务实现类
 * </p>
 *
 * @author cr
 * @since 2023-06-03
 */
@Slf4j
//@Service
public class FinancePayServiceImpl extends HztBaseService implements FinancePayService {

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private FinanceWxPayRecordMapper financeWxPayRecordMapper;

    @Autowired
    private FinanceAliPayRecordMapper financeAliPayRecordMapper;

    @Autowired
    private FinanceYPayRecordMapper financeYPayRecordMapper;

    @Autowired
    private FinanceUserWalletRecordService financeUserWalletRecordService;

    @Autowired
    private FinanceUserWalletService financeUserWalletService;

    @Autowired
    private FinanceCipherCodeMapper financeCipherCodeMapper;

    @Autowired
    private NotifyMsgCommonService notifyMsgService;

    @Override
    public WxPayVO createWxPayOrder(WxPayCreateOrderParam param) {
        WxPayConfigVO vo = sysConfigService.getWxPayConfig();
        PayToPointConfigVO payToPointConfigVO = validate(param.getAmount());
        BeanUtils.copyProperties(vo, param);
        FinanceWxPayRecordParam recordParam = WxPayUtils.createWxPayOrder(param);
        FinanceWxPayRecord financeWxPayRecord = BeanUtils.copyProperties(recordParam, FinanceWxPayRecord.class);
        financeWxPayRecord.setResultNo(RandomNumUtils.generatorCode("WX_RESULT"));
        financeWxPayRecord.setPoints(payToPointConfigVO.getPoints());
        financeWxPayRecordMapper.insert(financeWxPayRecord);
        WxPayVO payVO = new WxPayVO();
        payVO.setMsg(recordParam.getCodeUrl());
        payVO.setResultNo(financeWxPayRecord.getResultNo());
        return payVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void wxCallback(WxPayCallbackParam param) {
        log.info("微信支付回调参数:"+JSON.toJSONString(param));
        FinanceWxPayRecord wxPayRecord = financeWxPayRecordMapper.selectOne(FinanceWxPayRecord::getOutTradeNo, param.getOut_trade_no());
        if (Objects.isNull(wxPayRecord)) {
            throw ResultException.createResultException("订单记录不存在");
        }
        //如果不是未处理则直接返回
        if (!HandleStatusEnum.WAIT.eqValue(wxPayRecord.getHandleStatus())) {
            return;
        }
        if (WxTradeStateEnum.SUCCESS.eqValue(param.getTrade_state())) {
            wxPayRecord.setHandleStatus(HandleStatusEnum.DONE.getValue());
            wxPayRecord.setTransactionId(param.getTransaction_id());
            wxPayRecord.setTradeState(param.getTrade_state());
            wxPayRecord.setAmount(param.getTotalAmount());
            financeWxPayRecordMapper.updateById(wxPayRecord);
            FinanceUserWalletRecordSaveParam saveParam = new FinanceUserWalletRecordSaveParam();
            saveParam.setCustomerUserId(wxPayRecord.getCustomerUserId());
            saveParam.setPoints(wxPayRecord.getPoints().intValue());
            saveParam.setRecordType(RecordTypeEnum.WX_RECHARGE.getValue());
            financeUserWalletRecordService.saveWalletRecord(saveParam);
            WalletPointParam walletPointParam = new WalletPointParam();
            walletPointParam.setPoints(saveParam.getPoints());
            walletPointParam.setUserId(saveParam.getCustomerUserId());
            financeUserWalletService.updateWalletPoints(walletPointParam);

            // 发送用户通知
            notifyMsgService.publishUserMessage(saveParam.getCustomerUserId(),
                    String.format(UserNotifyEnum.POLITE_INVITATION.getMsg(),saveParam.getPoints().longValue()));
            //notifyMsgService.publishRechargeMessage(saveParam.getCustomerUserId(), saveParam.getPoints().longValue());
        }
    }

    @Override
    public String createAliPayOrder(AliPayCreateOrderParam param) {
        AliPayConfigVO vo = sysConfigService.getAliPayConfig();
        PayToPointConfigVO payToPointConfigVO = validate(param.getAmount());
        BeanUtils.copyProperties(vo, param);
        FinanceAliPayRecordParam recordParam = AliPayUtils.createAliPayOrder(param);
        FinanceAliPayRecord record = BeanUtils.copyProperties(recordParam, FinanceAliPayRecord.class);
        record.setPoints(payToPointConfigVO.getPoints());
        financeAliPayRecordMapper.insert(record);
        return recordParam.getBody();
    }


    @Override
    public String createAliPayAppOrder(AliPayCreateOrderParam param) {
        AliPayConfigVO vo = sysConfigService.getAliPayConfig();
        PayToPointConfigVO payToPointConfigVO = validate(param.getAmount());
        BeanUtils.copyProperties(vo, param);
        FinanceAliPayRecordParam recordParam = AliPayUtils.createAliPayAppOrder(param);
        FinanceAliPayRecord record = BeanUtils.copyProperties(recordParam, FinanceAliPayRecord.class);
        record.setPoints(payToPointConfigVO.getPoints());
        financeAliPayRecordMapper.insert(record);
        return recordParam.getBody();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aliPayCallback(Map<String,String> params) {
        String paramsJson = JSON.toJSONString(params);
        log.info("支付宝支付回调，{}", paramsJson);// 切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。//
        boolean flag = true;
        try  {
//            flag = AlipaySignature.rsaCheckV1(params, aliPayConfigVO.getAlipayPublicKey(), "UTF-8", "RSA2" );
            if(flag) {
                // 检测是否是真实数据   // 在此地处行数据持久化，支付宝返回的具体数据打印上文中处理好的paramsJson对象即可看到；

                AliPayCallBackParam aliPayCallBackParam = JSONUtil.toBean(paramsJson, AliPayCallBackParam.class);

                FinanceAliPayRecord aliPayRecord = financeAliPayRecordMapper.selectOne(FinanceAliPayRecord::getOutTradeNo, aliPayCallBackParam.getOut_trade_no());
                if (Objects.isNull(aliPayRecord)) {
                    throw ResultException.createResultException("订单记录不存在");
                }
                //如果不是未处理则直接返回
                if (!HandleStatusEnum.WAIT.eqValue(aliPayRecord.getHandleStatus())) {
                    return;
                }
                if (AliPayTradeStatusEnum.TRADE_SUCCESS.eqValue(aliPayCallBackParam.getTrade_status())) {
                    aliPayRecord.setHandleStatus(HandleStatusEnum.DONE.getValue());
                    aliPayRecord.setTradeNo(aliPayCallBackParam.getTrade_no());
                    aliPayRecord.setReceiptAmount(new BigDecimal(aliPayCallBackParam.getReceipt_amount()));
                    aliPayRecord.setTotalAmount(new BigDecimal(aliPayCallBackParam.getTotal_amount()));
                    aliPayRecord.setBuyerId(aliPayCallBackParam.getBuyer_id());
                    aliPayRecord.setTradeNo(aliPayCallBackParam.getTrade_no());
                    aliPayRecord.setGmtPayment(aliPayCallBackParam.getGmt_payment());
                    aliPayRecord.setSellerId(aliPayCallBackParam.getSeller_id());
                    financeAliPayRecordMapper.updateById(aliPayRecord);
                    FinanceUserWalletRecordSaveParam saveParam = new FinanceUserWalletRecordSaveParam();
                    saveParam.setCustomerUserId(aliPayRecord.getCustomerUserId());
                    saveParam.setPoints(aliPayRecord.getPoints().intValue());
                    saveParam.setRecordType(RecordTypeEnum.ALI_RECHARGE.getValue());
                    financeUserWalletRecordService.saveWalletRecord(saveParam);
                    WalletPointParam walletPointParam = new WalletPointParam();
                    walletPointParam.setPoints(saveParam.getPoints());
                    walletPointParam.setUserId(saveParam.getCustomerUserId());
                    financeUserWalletService.updateWalletPoints(walletPointParam);

                    // 发送用户通知
                    notifyMsgService.publishUserMessage(aliPayRecord.getCustomerUserId(),
                            String.format(UserNotifyEnum.POLITE_INVITATION.getMsg(),saveParam.getPoints().longValue()));

                    //notifyMsgService.publishRechargeMessage(aliPayRecord.getCustomerUserId(), saveParam.getPoints().longValue());
                }
            } else {
                log.info("返回数据存在风险，非合法的支付宝返回值！");}
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(flag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void createCipherCodePayOrder(FinanceCipherCodeParam param) {
        FinanceCipherCode cipherCode = financeCipherCodeMapper.selectOne(FinanceCipherCode::getCipherCode, param.getCipherCode());
        if (Objects.isNull(cipherCode)) {
            throw ResultException.createResultException("卡密不存在:{}", param.getCipherCode());
        }
        if (!CipherStatus.USABLE.eqValue(cipherCode.getStatus())) {
            throw ResultException.createResultException("卡密不可用:{}", cipherCode.getCipherCode());
        }
        cipherCode.setStatus(CipherStatus.USED.getValue());
        cipherCode.setUseUserId(UserThreadLocal.get().getUserId());
        financeCipherCodeMapper.updateById(cipherCode);
        FinanceUserWalletRecordSaveParam saveParam = new FinanceUserWalletRecordSaveParam();
        saveParam.setCustomerUserId(UserThreadLocal.get().getUserId());
        saveParam.setPoints(cipherCode.getPoints().intValue());
        saveParam.setRecordType(RecordTypeEnum.CIPHER_RECHARGE.getValue());
        financeUserWalletRecordService.saveWalletRecord(saveParam);
        WalletPointParam walletPointParam = new WalletPointParam();
        walletPointParam.setPoints(saveParam.getPoints());
        walletPointParam.setUserId(saveParam.getCustomerUserId());
        financeUserWalletService.updateWalletPoints(walletPointParam);

        // 发送用户通知
        notifyMsgService.publishUserMessage(saveParam.getCustomerUserId(),
                String.format(UserNotifyEnum.POLITE_INVITATION.getMsg(),saveParam.getPoints().longValue()));
        //notifyMsgService.publishRechargeMessage(saveParam.getCustomerUserId(), saveParam.getPoints().longValue());

    }

    @Override
    public FinanceCipherCodeVO getCipherCode(String code) {
        FinanceCipherCode cipherCode = financeCipherCodeMapper.selectOne(FinanceCipherCode::getCipherCode, code);
        if (Objects.isNull(cipherCode)) {
            throw ResultException.createResultException("卡密不存在:{}", code);
        }
        return BeanUtils.copyProperties(cipherCode, FinanceCipherCodeVO.class);
    }

    @Override
    public HztPage<FinanceUserWalletRecordVO> listUserWalletRecord(FinanceUserWalletRecordQueryParam param) {
        return financeUserWalletRecordService.listUserWalletRecord(param);
    }

    @Override
    public String createYPayOrder(YPayCreateOrderParam param, boolean isApp) {
        YPayConfigVO vo = sysConfigService.getYPayConfig();
        PayToPointConfigVO payToPointConfigVO = validate(param.getAmount());
        BeanUtils.copyProperties(vo, param);
        FinanceYPayRecordVO record = YPayUtil.createYPayOrder(param, isApp);
        record.setPoints(payToPointConfigVO.getPoints());
        financeYPayRecordMapper.insert(record);
        return record.getResponse();
    }

    @Override
    public String yPayCallback(Map<String, String> params) {
        String paramsJson = JSON.toJSONString(params);
        log.info("源支付回调，{}", paramsJson);
        YPayCallBackParam yPayCallBackParam = JSONUtil.toBean(paramsJson, YPayCallBackParam.class);

        FinanceYPayRecord yPayRecord = financeYPayRecordMapper.selectOne(FinanceYPayRecord::getOutTradeNo, yPayCallBackParam.getOut_trade_no());
        if (Objects.isNull(yPayRecord)) {
            throw ResultException.createResultException("订单记录不存在");
        }
        //如果不是未处理则直接返回
        if (!HandleStatusEnum.WAIT.eqValue(yPayRecord.getHandleStatus())) {
            throw ResultException.createResultException("支付未成功,请联系管理员");
        }
        YPayConfigVO vo = sysConfigService.getYPayConfig();
        if (AliPayTradeStatusEnum.TRADE_SUCCESS.eqValue(yPayCallBackParam.getTrade_status())) {
            yPayRecord.setHandleStatus(HandleStatusEnum.DONE.getValue());
            yPayRecord.setTradeNo(yPayCallBackParam.getTrade_no());
            yPayRecord.setTradeNo(yPayCallBackParam.getTrade_no());
            financeYPayRecordMapper.updateById(yPayRecord);
            FinanceUserWalletRecordSaveParam saveParam = new FinanceUserWalletRecordSaveParam();
            saveParam.setCustomerUserId(yPayRecord.getCustomerUserId());
            saveParam.setPoints(yPayRecord.getPoints().intValue());
            saveParam.setRecordType(RecordTypeEnum.Y_RECHARGE.getValue());
            financeUserWalletRecordService.saveWalletRecord(saveParam);
            WalletPointParam walletPointParam = new WalletPointParam();
            walletPointParam.setPoints(saveParam.getPoints());
            walletPointParam.setUserId(saveParam.getCustomerUserId());
            financeUserWalletService.updateWalletPoints(walletPointParam);
            // 发送用户通知
            notifyMsgService.publishUserMessage(yPayRecord.getCustomerUserId(),
                    String.format(UserNotifyEnum.POLITE_INVITATION.getMsg(),saveParam.getPoints().longValue()));
            //notifyMsgService.publishRechargeMessage(yPayRecord.getCustomerUserId(), saveParam.getPoints().longValue());
            return vo.getRedirectUrl();
        }
        throw ResultException.createResultException("支付未成功,请联系管理员");
    }

    @Override
    public Boolean getWxPayResult(String resultNo) {
        FinanceWxPayRecord financeWxPayRecord = financeWxPayRecordMapper.selectOne(FinanceWxPayRecord::getResultNo, resultNo, FinanceWxPayRecord::getCustomerUserId, UserThreadLocal.get().getUserId());
        if (Objects.isNull(financeWxPayRecord)) {
            return false;
        }

        return WxTradeStateEnum.SUCCESS.eqValue(financeWxPayRecord.getTradeState());
    }

    private PayToPointConfigVO validate(BigDecimal amount){
        List<PayToPointConfigVO> payToPointConfigVOS = sysConfigService.getPayToPointsConfig();
        if (CollectionUtils.isEmpty(payToPointConfigVOS)) {
            throw ResultException.createResultException("付款金额与配置金额不一致.");
        }

        for (PayToPointConfigVO payToPoint: payToPointConfigVOS) {
            if (payToPoint.getAmount().equals(amount)) {
                return payToPoint;
            }
        }
        throw ResultException.createResultException("付款金额与配置金额不一致.");
    }
}
