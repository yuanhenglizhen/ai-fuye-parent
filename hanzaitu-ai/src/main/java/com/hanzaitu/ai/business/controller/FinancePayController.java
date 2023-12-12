package com.hanzaitu.ai.business.controller;


import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.ai.business.param.AliPayCreateOrderParam;
import com.hanzaitu.ai.business.param.WxPayCallbackParam;
import com.hanzaitu.ai.business.param.WxPayCreateOrderParam;
import com.hanzaitu.ai.business.param.YPayCreateOrderParam;
import com.hanzaitu.ai.business.service.FinancePayService;
import com.hanzaitu.ai.util.WxPayCallbackUtil;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.page.TableDataInfo;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.params.FinanceCipherCodeParam;
import com.hanzaitu.common.params.FinanceUserWalletRecordQueryParam;
import com.hanzaitu.common.service.ISysConfigService;
import com.hanzaitu.common.vo.FinanceUserWalletRecordVO;
import com.hanzaitu.common.vo.WxPayConfigVO;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.hanzaitu.common.core.controller.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 微信支付记录 前端控制器
 * </p>
 *
 * @author cr
 * @since 2023-06-03
 */
//@Slf4j
//@RestController
//@RequestMapping("/financePay")
//public class FinancePayController extends BaseController {
//
//    @Autowired
//    private FinancePayService financePayService;
//
//    @Autowired
//    private Verifier verifier;
//
//    @Autowired
//    private ISysConfigService sysConfigService;
//    @ApiOperation(value = "创建微信支付订单", httpMethod = "POST")
//    @PostMapping("createWxPayOrder")
//    public AjaxResult createWxPayOrder(@RequestBody WxPayCreateOrderParam param) {
//        return AjaxResult.success(financePayService.createWxPayOrder(param));
//    }
//
//    @PassPath
//    @ApiOperation(value = "微信支付订单的callback", httpMethod = "POST")
//    @PostMapping("wxCallback")
//    public AjaxResult wxCallback(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            WxPayConfigVO wxPayConfigVO = sysConfigService.getWxPayConfig();
//            return AjaxResult.success(WxPayCallbackUtil.wxPaySuccessCallback(request, response, verifier, wxPayConfigVO, callbackData -> {
//                // TODO 处理你的业务逻辑，下面说一下一般业务逻辑处理方法
//                log.info("微信支付返回的信息：{}", callbackData);
//                // 1.根据订单id获取订单信息
//                financePayService.wxCallback(callbackData);
//                // 2.判断金额是否相符，如果不相符则调用退款接口，并取消该订单，通知客户支付金额不符
//
//                // 3.查询订单状态是否是未支付，如果是未支付则改为已支付，填充其他逻辑，
//
//                // 4.如果是其他状态综合你的业务逻辑来处理
//
//                // 5.如果是虚拟物品，则对应充值，等等其他逻辑
//            }));
//        } catch (Exception e) {
//           e.printStackTrace();
//           logger.info("通知签名验证失败");
//        }
//        return AjaxResult.success();
//    }
//
//    @ApiOperation(value = "微信支付订单的callback", httpMethod = "POST")
//    @PostMapping("wxCallbackTest")
//    public AjaxResult wxCallbackTest(@RequestBody WxPayCallbackParam param) {
//        financePayService.wxCallback(param);
//        return AjaxResult.success();
//    }
//
//    @ApiOperation(value = "创建支付宝支付订单", httpMethod = "POST")
//    @PostMapping("createAliPayOrder")
//    public AjaxResult createAliPayOrder(@RequestBody AliPayCreateOrderParam param) {
//        return AjaxResult.success(financePayService.createAliPayOrder(param));
//    }
//
//    @ApiOperation(value = "创建支付宝app支付订单", httpMethod = "POST")
//    @PostMapping("createAliPayAppOrder")
//    public AjaxResult createAliPayAppOrder(@RequestBody AliPayCreateOrderParam param) {
//        return AjaxResult.success(financePayService.createAliPayAppOrder(param));
//    }
//
//
//    @PassPath
//    @ApiOperation(value = "支付宝支付订单的callback", httpMethod = "POST")
//    @PostMapping("/aliPayCallback")
//    public void aliPayCallback(HttpServletRequest request){
//        logger.info("服务端验证异步通知信息参数");
//        // 需要注意的是这个公钥是支付宝公钥（沙箱测试环境获取支付宝公钥的地方请看下方截图，正式企业开发），并不是上面的公钥，需要特别注意！！！！！！！！！！！
//        String alipaypublicKey = "xxxxxxxxx";
//        String charset = "utf-8";
//        // 获取支付宝POST过来反馈信息
//        Map<String,String> params = new HashMap<String,String>();
//        Map requestParams = request.getParameterMap();
//        try {
//            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
//                String name = (String) iter.next();
//                String[] values = (String[]) requestParams.get(name);
//                String valueStr = "";
//                for (int i = 0; i < values.length; i++) {
//                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
//                }//乱码解决，这段代码在出现乱码时使用。
//                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//                params.put(name, valueStr);
//            }
//            financePayService.aliPayCallback(params);
//        } catch (Exception e) {
//            throw ResultException.createResultException(e.getMessage());
//        }
//    }
//
//    @ApiOperation(value = "卡密充值", httpMethod = "POST")
//    @PostMapping("createCipherCodePayOrder")
//    public AjaxResult createCipherCodePayOrder(@Validated @RequestBody FinanceCipherCodeParam param) {
//        financePayService.createCipherCodePayOrder(param);
//        return AjaxResult.success();
//    }
//
//    @ApiOperation(value = "卡密信息", httpMethod = "GET")
//    @GetMapping("getCipherCode")
//    public AjaxResult getCipherCode(String code) {
//        return AjaxResult.success(financePayService.getCipherCode(code));
//    }
//
//    @ApiOperation(value = "获取钱包记录", httpMethod = "POST")
//    @PostMapping("listUserWalletRecord")
//    public AjaxResult listUserWalletRecord(@RequestBody FinanceUserWalletRecordQueryParam param) {
//        return AjaxResult.success(financePayService.listUserWalletRecord(param));
//    }
//
//    @ApiOperation(value = "获取支付配置", httpMethod = "GET")
//    @GetMapping("listPayConfig")
//    public AjaxResult listPayConfig() {
//        return AjaxResult.success(sysConfigService.getPayToPointsConfig());
//    }
//
//    @ApiOperation(value = "创建源支付订单", httpMethod = "POST")
//    @PostMapping("createYPayAppOrder")
//    public AjaxResult createYPayAppOrder(@RequestBody YPayCreateOrderParam param) {
//        return AjaxResult.success(financePayService.createYPayOrder(param, true));
//    }
//
//    @ApiOperation(value = "创建源支付网页订单", httpMethod = "POST")
//    @PostMapping("createYPayWebOrder")
//    public AjaxResult createYPayWebOrder(@RequestBody YPayCreateOrderParam param) {
//        return AjaxResult.success(financePayService.createYPayOrder(param, false));
//    }
//
//    @PassPath
//    @ApiOperation(value = "源支付订单的callback", httpMethod = "GET")
//    @GetMapping("/yPayCallback")
//    public void yPayCallback(HttpServletRequest request, HttpServletResponse response){
//        logger.info("服务端验证异步通知信息参数");
//        // 获取支付宝POST过来反馈信息
//        Map<String,String> params = new HashMap<String,String>();
//        Map requestParams = request.getParameterMap();
//        try {
//            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
//                String name = (String) iter.next();
//                String[] values = (String[]) requestParams.get(name);
//                String valueStr = "";
//                for (int i = 0; i < values.length; i++) {
//                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
//                }//乱码解决，这段代码在出现乱码时使用。
//                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//                params.put(name, valueStr);
//            }
//            String redirectUrl = financePayService.yPayCallback(params);
//            response.sendRedirect(redirectUrl);
//        } catch (Exception e) {
//            throw ResultException.createResultException(e.getMessage());
//        }
//    }
//
//
//    @ApiOperation(value = "获取微信支付结果", httpMethod = "GET")
//    @GetMapping("/getWxPayResult")
//    public AjaxResult getWxPayResult(String resultNo){
//        return AjaxResult.success(financePayService.getWxPayResult(resultNo));
//    }
//}
