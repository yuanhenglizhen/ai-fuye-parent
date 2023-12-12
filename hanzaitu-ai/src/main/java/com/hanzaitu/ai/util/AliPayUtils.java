package com.hanzaitu.ai.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.hanzaitu.ai.business.enums.HandleStatusEnum;
import com.hanzaitu.ai.business.param.AliPayCreateOrderParam;
import com.hanzaitu.ai.business.param.FinanceAliPayRecordParam;
import com.hanzaitu.common.constant.Constants;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.utils.RandomNumUtils;
import com.hanzaitu.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class AliPayUtils {

    /**
     * 生产支付宝支付订单
     * @param param
     * @return
     */
    public static FinanceAliPayRecordParam createAliPayOrder(AliPayCreateOrderParam param){
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(param.getAppId());
        alipayConfig.setPrivateKey(param.getPrivateKey());
        alipayConfig.setAlipayPublicKey(param.getAlipayPublicKey());
        FinanceAliPayRecordParam recordParam= new FinanceAliPayRecordParam();
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(createTradeNo());
            model.setTotalAmount(param.getAmount().toString());
            model.setSubject("充值:"+param.getAmount());
            model.setProductCode(Constants.ALIPAY_PRODUCT_CODE);
            request.setBizModel(model);
            request.setNotifyUrl(param.getNotifyUrl());
            request.setReturnUrl(param.getReturnUrl());
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (!response.isSuccess()) {
                throw ResultException.createResultException("调用失败:",response.getBody());
            }
            log.info("创建支付宝订单:{}", response.getBody());
            recordParam.setHandleStatus(HandleStatusEnum.WAIT.getValue());
            recordParam.setBody(response.getBody());
            recordParam.setTotalAmount(param.getAmount());
            recordParam.setOutTradeNo(model.getOutTradeNo());
            recordParam.setCustomerUserId(UserThreadLocal.get().getUserId());
        } catch (Exception e) {
            throw ResultException.createResultException("调用失败:",e.getMessage());
        }
        return recordParam;
    }


    /**
     * 生产支付宝app支付订单
     * @param param
     * @return
     */
    public static FinanceAliPayRecordParam createAliPayAppOrder(AliPayCreateOrderParam param){
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(param.getAppId());
        alipayConfig.setPrivateKey(param.getPrivateKey());
        alipayConfig.setAlipayPublicKey(param.getAlipayPublicKey());
        FinanceAliPayRecordParam recordParam= new FinanceAliPayRecordParam();
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(createTradeNo());
            model.setTotalAmount(param.getAmount().toString());
            model.setSubject("Pay:"+param.getAmount());
            model.setProductCode(Constants.ALIPAY_PRODUCT_CODE);
            request.setBizModel(model);
            request.setNotifyUrl(param.getNotifyUrl());
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if (!response.isSuccess()) {
                throw ResultException.createResultException("调用失败:",response.getBody());
            }
            log.info("创建支付宝app订单:{}", response.getBody());
            recordParam.setHandleStatus(HandleStatusEnum.WAIT.getValue());
            recordParam.setBody(response.getBody());
            recordParam.setTotalAmount(param.getAmount());
            recordParam.setOutTradeNo(model.getOutTradeNo());
            recordParam.setCustomerUserId(UserThreadLocal.get().getUserId());
        } catch (Exception e) {
            throw ResultException.createResultException("调用失败:",e.getMessage());
        }
        return recordParam;
    }

//    static void test() throws AlipayApiException {
//        AlipayConfig alipayConfig = new AlipayConfig();
//        alipayConfig.setAppId("2021003199620473");
//        alipayConfig.setPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCs/MZIYtPjQMyMJ/WM/SGX8A3Z2RfirckcT5N7uLCy+klmJJo23z/GEP3uHCvo9UzgitBp/ony4vR3JxivYki4jFVeV3Fd2GC8QN6khV0yphqj32frzCCGKaMdhSFGC5zHX2l9CEzyv99x2zfT5kT58vvaQ7ldygLo8ysXyj7ahTxbgq31gY0sXbfjwEMUFGHXP2WfTOWgFypudrPOfsXPnCkuVnJBUGyVuvq+E9Yg9TRbCM0hnfZFGjS9WpEz907bC61HcJ1Kv7r/s9z+YF9jYJWoEd9SLlPSbDU2n3sDWNNot7ILRpH9ohSCIq9H5vl1CQLaUAWTs4jST7oOfoRAgMBAAECggEANBqkmKokrSvb0FBOB8hifIcDS5LuEqBr7h0u50aKI3nCkfhGtYJ6S/9SaRif44T+YzBLKjtaoC6mqJy3wy0n1D4zyI0/co7q0pZd+qjt4Mc7B4/4VoYyYkqNaoVQHNbM/ZsOEfBGpC0SuQOdRVCoB6VqSSCqjZN45VsBJt4wqjjG3qihoIhWNg9mnH0XT1pzNlhsdODLhxCfecRNna3E9jjYgSrPvxKymYOW5zqvY6suBvgP/DttH3RNxZR3Tg0DEi8zrjBGPPeXmK8ykRpiaJISEXBjxSwVS1f4qUPHmgpJTYhv2Qww92/FLKX1S/V94guXHDRCN7utMW++nwUE2QKBgQDQEHNBmnAO+7nkFPnWEcUnnQvGl9Ibc5ls4VLHlFQkXt75eW0HJJbyuuT5D7n8ks+Wh89RDltRxy9PTE8ChDC/hgnEkh5BvH0/iKZDrFQTFPcu6yvp65/Vu4be3iyL0RFcQqBSLim317Va/Yo5wCwG6T3kU8m6Wa2vWrvd4EH9cwKBgQCg0MIHZ0LSBdxsrPRVo0JpsmPYEngPEGua9jQq00/zuW7cpN37GiXSNTbMwE0X+hAFRwZc4DE16l9qDXUwDT54eL9ivCHQRkBjkmQssM5V8RP1R2ka0yLHoxLgD8J+rcoKiHmC2OGPM9QrswoazWGLT2QMfepIOItbrldoS1QJawKBgAQgj2i5u4dQjvtrPxlz+Mk0YEtsL4rD3ENApPe4hYLDLwEb24HMxAHEm04ldbcA4oVF5GLjOgSBQvr7tIWEQcNK0V7I1zjOmF1uRH4LKQ/H1q9jktdtCRqgQaiiF5geaY2+9Jcsuz8eVEadyOQkX7szXTruPo4nwT49OoTIMgbpAoGAJNCFiqJWBrR2yJ4zdSNhwcan2sAnofM1Xw071g34AKiTu4k2lBRhUiUCfY8MVw/U+RBQYZSlT11vKF0aVbzCb1vdHGJLL13VwbWynxR+t+fvlLgbVJu/G4q2eHrDILLyTMT/ZDf4Mws/aB2dk5Mu4HB1S/LLXmsrY/qeYe7Uty8CgYEAkLjBiYdl7tSOaZQ5pO25y6h+hfl/q0SsVRNJsgjauKQZFOiJtUhV5hbr9EuM3CL9MPXuN2Wf/+gIPJCrfKa5SqTAiDoZTByU9PeVHUXGylCPh58FmgGaAPIpjqC+Q6aFjCzLGU6sPSpdRitUQHJ8VY79p/3rc9V1++UDzKNm7nU=");
//        alipayConfig.setAlipayPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoC+LzrKI3E55rEW7TUN567o6VCVUKWgIm8lDHRTyfolMEP8s89gK0HxmB0I0ba3QZ6sxtoJ3ZcoeFFVg7UAPXPevVEKEU6bEe4uWDzcXKxQCWsi38Mti612tyc+1iSod2/PsokBXFWqcfo6hYEPyVcgZJdtBZV/PSOudd9BjeCw6Sm/qDc2PPaj1SGXor4B+pgYvTpO+6KUmF8uxM+nZqRQpvTixZxBN95WB5mVoy6AJcFCWnWw3BlNnQ8TH+U2eofTkAk2fHiYLwQi49JcFmq6F286+dQTRCFzQFvW486dF0E37sfV0rL7ZDKm1CBpaIJ+vcPjhkJriaUgnGzPOzwIDAQAB");
//
//        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
//        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
//        model.setOutTradeNo(createTradeNo());
//        model.setTotalAmount("0.1");
//        model.setSubject("测试");
//        model.setProductCode("FAST_INSTANT_TRADE_PAY");
//        request.setNotifyUrl("https://www.baidu.com");
//        request.setBizModel(model);
//        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
//        System.out.println(response.getBody());
//        if (response.isSuccess()) {
//            System.out.println("调用成功");
//        } else {
//            System.out.println("调用失败");
//        }
//    }

    private static String createTradeNo(){
        Long userId = UserThreadLocal.get().getUserId();
        Long timezone = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return StringUtils.join(userId+timezone+ RandomNumUtils.getRandomSeed()+userId);
    }

    public static void main(String[] args) throws AlipayApiException {
        System.out.printf(AlipaySignature.sign("123","MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDT8lfA8/QGi7t4Lu3jlDFomBBCY8uhTTC/gtycW3HQ2OYF7+dBD3iMHMh/WOsxawggCzO0DfVfDaCwHHoHdVIy6uoZnFrxhk+rbmODE9Ypa4lFlULSqXJF02ft33NVEzAhSCyCn6Ke1/pzkb7DcWRyK+ZNTgzLKiEj8Q+QfCqhy5Zc8jYCcDUWqe5A1ZPI1y/IFBpiviK90cv2AwWbi2rqLKPDPvQdaB3eNSVpJ7z37Vn/0Bm9hTfASsSLPruvC3sBIyvbAEbpa5/Vqx5qi6ogWNHcU/xmNDxQH+ETfmAvC8d98Ocz4PxQz/3bmlsioXuM41JWU444YoI+24TOlO41AgMBAAECggEAb9xe3lKi/3VuOy8hlNRZA2gGZNPA3GAeE2Wgw9Erj9t6A7YralYc/G4Qj83BS/K+dhMTdbtMPp+1qF1PNnKAMKl1ZpD0GrcVg1geo0ar6G5+I8B0ZTTTP/Ek0BH4D/WxmE88VrCkJou8/pP0h7hoGC39Pd2suOmCVjLbURmHcp5HxaxQ3k2jPKdZxxrpFPJkAyC2KodvpWY4o9sDwoTD1vpWWn+8kpv1z22xukEktNxbyX9NrtWx0BFsccvkAznTWQo/gDxOIVNZkymyiyd2eA0d4vXwKAm+2CeiiVCgilrRJZDeis29/yoPle0xJY1xnTbf5o0Qt2sq0tXft5j4vQKBgQDvL96QepHBidusGdOSgv9/15uwom/bNw65zdhR9mwoZUaYmlCW5AejLrTfEi8h4K2pFHfU4BEm23TuPlFciDJ8Q2NS8atOc60RFBHQcbqDniYBJgKVf9pc0qR/zgaVbOLqp1OSUOWSwLBWEltjsDRrtvYxrtTfZRzBQ3Wf1t8wKwKBgQDi2Em5H+DeGHJ3L5w+maZyiQDw1+0KirFpUBWB70NvKkE9GrhbamnLU3xUKmE+hiXZRY6sgqj8tmuvczJDcNiTjPiaY9SfqcGlCZaZEMoXrDujUe/Uw2sLbor0UZcC52/SVUz9pM1x62yk/vcqwujgED588a1jz4GRilMkoRTLHwKBgCZZuonX3T725630hiPbtTO+UkxD/0Na1JWeF60sobLoaiVVuM44ybC0cHPD6qK376BIk94h/x4H2fvJKip3/ABInchSz3DIw1sVqLWPRqSL2vQcMBXgZhaz/4QueIfE6a1OHuw65FRfaOakEtstCXqr31Wf/qnDAJVMR7PVM/v5AoGBAIn/5FGfqA/UtYX2S8n1dUHGQPFDmqvIFgeoNX1XTiHJheijYbYTNYsBHeih44jC2TGlVBd0YVNjsn0IynImq0qP/wkFZGtBPVGmVJyHjzn5iEph5VH9FBjfRd89521X2DoPtIe1g1ojS3PQvBKigkMU/r7J056dtQgSK2RkLh8nAoGAJdnQZfkqnj7pan/8DOvXltkTBe1hPYAWCOZQHSF9fk4gnYp9be517AcIS7CGLp0FR2n510bkqHzbXNOtSG/2V+LyHvCXyP8Ya6VtyTmCQmyjotCDy1/DVxgAhqqtiCfhcZvensGdW64HZFeIaouNCSY070LgTqIB5Goz+DPHorM=","utf-8","RSA2"));
    }
}
