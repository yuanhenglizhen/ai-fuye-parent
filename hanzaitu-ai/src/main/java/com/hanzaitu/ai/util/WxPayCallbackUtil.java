package com.hanzaitu.ai.util;

import cn.hutool.json.JSONUtil;
import com.hanzaitu.ai.business.param.WxPayCallbackParam;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.utils.http.HttpUtils;
import com.hanzaitu.common.vo.WxPayConfigVO;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class WxPayCallbackUtil {

    /**
     * 微信支付创建订单回调方法
     * @param verifier 证书
     * @param wxPayConfig 微信配置
     * @param businessCallback 回调方法，用于处理业务逻辑
     * @return json格式的string数据，直接返回给微信
     */
    public static String wxPaySuccessCallback(HttpServletRequest request, HttpServletResponse response, Verifier verifier, WxPayConfigVO wxPayConfig, Consumer<WxPayCallbackParam> businessCallback) {

        // 1.处理通知参数
        final String body = HttpUtils.readData(request);
        HashMap<String, Object> bodyMap = JSONUtil.toBean(body, HashMap.class);
        // 2.签名验证
        WechatPayValidatorForUtil wechatForRequest = new WechatPayValidatorForUtil(verifier, body, (String) bodyMap.get("id"));
        try {
            if (!wechatForRequest.validate(request)) {
                // 通知验签失败
                response.setStatus(500);
                final HashMap<String, Object> map = new HashMap<>();
                map.put("code", "ERROR");
                map.put("message", "通知验签失败");
                return JSONUtil.toJsonStr(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 3.获取明文数据
        String plainText = decryptFromResource(bodyMap,wxPayConfig);
        HashMap<String,Object> plainTextMap = JSONUtil.toBean(plainText, HashMap.class);
        log.info("plainTextMap:{}",plainTextMap);
        // 4.封装微信返回的数据
        WxPayCallbackParam callbackData = new WxPayCallbackParam();
//        callbackData.setSuccess_time(String.valueOf(plainTextMap.get("success_time")));
        callbackData.setOut_trade_no(String.valueOf(plainTextMap.get("out_trade_no")));
        callbackData.setTransaction_id(String.valueOf(plainTextMap.get("transaction_id")));
        callbackData.setTrade_state(String.valueOf(plainTextMap.get("trade_state")));
        callbackData.setTrade_type(String.valueOf(plainTextMap.get("trade_type")));
        String amount = String.valueOf(plainTextMap.get("amount"));
        HashMap<String,Object> amountMap = JSONUtil.toBean(amount, HashMap.class);
        String total = String.valueOf(amountMap.get("total"));
        callbackData.setTotalAmount(new BigDecimal(total).movePointLeft(2));
        log.info("callbackData:{}",callbackData);

        if ("SUCCESS".equals(callbackData.getTrade_state())) {
            // 执行业务逻辑
            businessCallback.accept(callbackData);
        }

        // 5.成功应答
        response.setStatus(200);
        final HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", "SUCCESS");
        resultMap.put("message", "成功");
        return JSONUtil.toJsonStr(resultMap);
    }

    /**
     * 对称解密
     */
    private static String decryptFromResource(HashMap<String, Object> bodyMap, WxPayConfigVO wxPayConfig) {
        // 通知数据
        Map<String, String> resourceMap = (Map) bodyMap.get("resource");
        // 数据密文
        String ciphertext = resourceMap.get("ciphertext");
        // 随机串
        String nonce = resourceMap.get("nonce");
        // 附加数据
        String associateData = resourceMap.get("associated_data");
        AesUtil aesUtil = new AesUtil(wxPayConfig.getApiV3key().getBytes(StandardCharsets.UTF_8));
        try {
            return aesUtil.decryptToString(associateData.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw ResultException.createResultException("解密失败");
        }
    }
}
