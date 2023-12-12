package com.hanzaitu.ai.util;

import cn.hutool.http.HttpUtil;
import com.alipay.api.internal.util.AlipaySignature;
import com.hanzaitu.ai.business.enums.HandleStatusEnum;
import com.hanzaitu.ai.business.param.YPayCreateOrderParam;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.FinanceYPayRecord;
import com.hanzaitu.common.utils.MD5Utils;
import com.hanzaitu.common.utils.RandomNumUtils;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.http.HttpUtils;
import com.hanzaitu.common.utils.sign.Md5Utils;
import com.hanzaitu.common.vo.FinanceYPayRecordVO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class YPayUtil {

    /**
     * 生产支付宝支付订单
     * @param param
     * @return
     */
    public static FinanceYPayRecordVO createYPayOrder(YPayCreateOrderParam param, boolean isApp){

        FinanceYPayRecordVO recordParam= new FinanceYPayRecordVO();
        try {
            String tradeNo = createTradeNo();
            StringBuffer params = new StringBuffer();
            params.append("money=").append(param.getAmount())
                    .append("&name=商品")
                    .append("&notify_url=").append(param.getNotifyUrl())
                    .append("&out_trade_no=").append(tradeNo)
                    .append("&pid=").append(param.getMerchantId())
                    .append("&return_url=").append(param.getReturnUrl())
                    .append("&type=").append(param.getPayType());
            String sign= Md5Utils.hash(params.toString()+param.getToken());//.doMD5Sign(params.toString());
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("pid",param.getMerchantId());
            paramMap.put("type",param.getPayType());
            paramMap.put("out_trade_no",tradeNo);
            paramMap.put("notify_url",param.getNotifyUrl());
            paramMap.put("return_url",param.getReturnUrl());
            paramMap.put("name","商品");
            paramMap.put("money",param.getAmount().toString());
            paramMap.put("sign",sign);
            paramMap.put("sign_type","MD5");
//            params.append("").append("");
            params.append("&sign=").append(sign).append("&sign_type=MD5");
            String url = param.getPostWebUrl();
            if (isApp) {
                url = param.getPostUrl();
            }
            String response = HttpUtil.post(url, paramMap);
            log.info("创建源支付订单:{}", response);
            if (response.contains("/Pay/console")) {
                response = response.replace("/Pay/console","http://pay.hanzaitu.com/Pay/console");
            }
            recordParam.setResponse(response);
            recordParam.setHandleStatus(HandleStatusEnum.WAIT.getValue());
            recordParam.setTotalAmount(param.getAmount());
            recordParam.setOutTradeNo(tradeNo);
            recordParam.setCustomerUserId(UserThreadLocal.get().getUserId());
        } catch (Exception e) {
            throw ResultException.createResultException("调用失败:",e.getMessage());
        }
        return recordParam;
    }


    private static String createTradeNo(){
        Long userId = UserThreadLocal.get().getUserId();
        Long timezone = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return StringUtils.join(userId+timezone+ RandomNumUtils.getRandomSeed()+userId);
    }
}
