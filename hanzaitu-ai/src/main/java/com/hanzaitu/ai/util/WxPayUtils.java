package com.hanzaitu.ai.util;


import com.hanzaitu.ai.business.enums.HandleStatusEnum;
import com.hanzaitu.ai.business.param.FinanceWxPayRecordParam;
import com.hanzaitu.ai.business.param.WxPayCreateOrderParam;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.service.ISysConfigService;
import com.hanzaitu.common.utils.RandomNumUtils;
import com.hanzaitu.common.utils.SpringApplicationUtils;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.vo.WxPayConfigVO;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
//@Component
public class WxPayUtils  implements InitializingBean {

    @Autowired
    private ISysConfigService sysConfigService;

    private static Config config;

    @Autowired
    private ResourceLoader resourceLoader;
    @Override
    public void afterPropertiesSet() throws IOException {
        WxPayConfigVO configVO = sysConfigService.getWxPayConfig();
        log.debug("Bean初始化完成，获取到路径{}",configVO.getPrivateKeyPath());
        String path = configVO.getPrivateKeyPath();
        log.debug("当前模式{}",SpringApplicationUtils.getDevMode());
        if (SpringApplicationUtils.getDevMode()) {
            Resource resource = resourceLoader.getResource(privateKeyPath);
            path = resource.getFile().getPath();
        }
        this.config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(configVO.getMerchantId())
                        .privateKeyFromPath(path)
                        .merchantSerialNumber(configVO.getMerchantSerialNumber())
                        .apiV3Key(configVO.getApiV3key())
                        .build();

    }

    /** 商户号 */
    public static String merchantId = "1634548238";

    /** 商户API私钥路径 */
    public static String privateKeyPath = "classpath:config\\apiclient_key.pem";

    /** 商户证书序列号 */
    public static String merchantSerialNumber = "5194D487C045FE80C54B33465014A6031C47D406";
    /** 商户APIV3密钥 */
    public static String apiV3key = "KuG4ZjODHEyfYA9pSOWDiTLf76Hc1IYS";

    public static FinanceWxPayRecordParam createWxPayOrder(WxPayCreateOrderParam param){

        // 构建service
        NativePayService service = new NativePayService.Builder().config(config).build();
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(param.getAmount().multiply(new BigDecimal(100)).intValue());
        amount.setCurrency("CNY");
        request.setAmount(amount);
        request.setAppid(param.getAppId());
        request.setMchid(param.getMerchantId());
        request.setDescription("充值:"+param.getAmount());
        request.setNotifyUrl(param.getNotifyUrl());
        request.setOutTradeNo(createTradeNo());
        // 调用下单方法，得到应答
        PrepayResponse response = service.prepay(request);
        // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
        FinanceWxPayRecordParam record = new FinanceWxPayRecordParam();
        record.setHandleStatus(HandleStatusEnum.WAIT.getValue());
        record.setAmount(param.getAmount());
        record.setCustomerUserId(UserThreadLocal.get().getUserId());
        record.setOutTradeNo(request.getOutTradeNo());
        record.setCodeUrl(response.getCodeUrl());
        return record;
    }
//    public static void setup() throws IOException {
//        Config config =
//                new RSAAutoCertificateConfig.Builder()
//                        .merchantId(merchantId)
//                        .privateKeyFromPath(privateKeyPath)
//                        .merchantSerialNumber(merchantSerialNumber)
//                        .apiV3Key(apiV3key)
//                        .build();
//        // 构建service
//        NativePayService service = new NativePayService.Builder().config(config).build();
//        // request.setXxx(val)设置所需参数，具体参数可见Request定义
//        PrepayRequest request = new PrepayRequest();
//        Amount amount = new Amount();
//        amount.setTotal(1);
//        amount.setCurrency("人民币");
//        request.setAmount(amount);
//        request.setAppid("wxc7fafed7a38423ff");
//        request.setMchid("1634548238");
//        request.setDescription("测试商品标题");
//        request.setNotifyUrl("https://www.baidu.com");
//        request.setOutTradeNo("out_trade_no_001");
//        // 调用下单方法，得到应答
//        PrepayResponse response = service.prepay(request);
//        // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
//        System.out.println(response.getCodeUrl());
//    }
//
//    public static void main(String[] args) throws IOException {
//        WxPayUtils.setup();
//    }


    private static String createTradeNo(){
        Long userId = UserThreadLocal.get().getUserId();
        Long timezone = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return StringUtils.join(userId+timezone+ RandomNumUtils.getRandomSeed()+userId);
    }

    /**
     * 获取商户的私钥文件
     *
     * @param filename 证书地址
     * @return 私钥文件
     */
    public PrivateKey getPrivateKey(String filename) {
        try {
            return PemUtil.loadPrivateKey(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw ResultException.createResultException("私钥文件不存在");
        }
    }

    /**
     * 获取签名验证器
     */
    @Bean
    public Verifier getVerifier() throws IOException {
        WxPayConfigVO configVO = sysConfigService.getWxPayConfig();
        String path = configVO.getPrivateKeyPath();

        if (SpringApplicationUtils.getDevMode()) {
            Resource resource = resourceLoader.getResource(privateKeyPath);
            path = resource.getFile().getPath();
        }
        // 获取商户私钥
        final PrivateKey privateKey = getPrivateKey(path);

        // 私钥签名对象
        PrivateKeySigner privateKeySigner = new PrivateKeySigner(configVO.getMerchantSerialNumber(), privateKey);

        // 身份认证对象
        WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(configVO.getMerchantId(), privateKeySigner);

        // 获取证书管理器实例
        CertificatesManager certificatesManager = CertificatesManager.getInstance();

        try {
            // 向证书管理器增加需要自动更新平台证书的商户信息
            certificatesManager.putMerchant(configVO.getMerchantId(), wechatPay2Credentials, configVO.getApiV3key().getBytes(StandardCharsets.UTF_8));
        } catch (IOException | GeneralSecurityException | HttpCodeException e) {
            e.printStackTrace();
        }

        try {
            return certificatesManager.getVerifier(configVO.getMerchantId());
        } catch (NotFoundException e) {
            e.printStackTrace();
            log.error(e.toString());
            return null;
            //throw ResultException.createResultException("获取签名验证器失败");
        }
    }

//    /**
//     * 获取微信支付的远程请求对象
//     *
//     * @return Http请求对象
//     */
//    @Bean
//    public CloseableHttpClient getWxPayClient() {
//        WxPayConfigVO configVO = sysConfigService.getWxPayConfig();
//        // 获取签名验证器
//        Verifier verifier = getVerifier();
//
//        // 获取商户私钥
//        final PrivateKey privateKey = getPrivateKey(configVO.getPrivateKeyPath());
//        this.config =
//                new RSAAutoCertificateConfig.Builder()
//                        .merchantId(configVO.getMerchantId())
//                        .privateKeyFromPath(configVO.getPrivateKeyPath())
//                        .merchantSerialNumber(configVO.getMerchantSerialNumber())
//                        .apiV3Key(configVO.getApiV3key())
//                        .build();
//        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create().withMerchant(configVO.getMerchantId(), configVO.getMerchantSerialNumber(), privateKey)
//                .withValidator(new WechatPay2Validator(verifier));
//
//        return builder.build();
//    }
}
