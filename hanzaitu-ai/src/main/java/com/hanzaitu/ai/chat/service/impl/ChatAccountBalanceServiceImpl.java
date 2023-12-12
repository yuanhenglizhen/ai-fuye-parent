package com.hanzaitu.ai.chat.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.hanzaitu.ai.chat.service.ChatAccountBalanceService;
import com.hanzaitu.common.utils.DateUtils;
import com.plexpt.chatgpt.entity.billing.SubscriptionData;
import com.plexpt.chatgpt.entity.billing.UseageResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.http.HttpRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Service
public class ChatAccountBalanceServiceImpl implements ChatAccountBalanceService {

    static String URL_SUB = "v1/dashboard/billing/subscription";

    static String URL_USEAGE = "v1/dashboard/billing/usage";

    @SneakyThrows
    private BigDecimal getBalance(String key, Proxy proxy) {

        try {
            String host = "https://api.openai.com/";
            String res = getSub(key, host, proxy);

            String res2 = getUseage(key, host, proxy);

            if (org.apache.commons.lang3.StringUtils.contains(res, "The server had an error processing your request.")) {
                log.error("服务器错误，请重试");
                return null;
            }

            if (org.apache.commons.lang3.StringUtils.contains(res, "Incorrect API key provided:")) {
                log.error("KEY错误，重试也没用");

                return null;
            }
            if (org.apache.commons.lang3.StringUtils.contains(res, "This key is associated with a deactivated account")) {
                log.error("KEY被ban了，重试也没用");

                return null;
            }


            SubscriptionData balanceDTO = JSON.parseObject(res, SubscriptionData.class);
            BigDecimal total = balanceDTO.getHardLimitUsd();
            UseageResponse useageResponse = JSON.parseObject(res2, UseageResponse.class);
            BigDecimal used = useageResponse.getTotalUsage()
                    .divide(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal balance = total.subtract(used);

            return balance;
        } catch (Exception e) {
            if (e instanceof SocketException) {
                log.error("同步key余额出错：{}",e.getMessage());
            }
            if (e instanceof SocketTimeoutException) {
                //throw e;
                log.error("同步key余额出错：{}",e.getMessage());
            }

            //e.printStackTrace();
            log.warn("同步key余额出错：" + e);
        }
        return null;
    }

    private static String getSub(String key, String host, Proxy proxy) {

        String res = HttpRequest.get(host + URL_SUB)
                .setHeader("Authorization", "Bearer " + key)
                .setHeader("Content-type", "application/json")
                .proxy(proxy)
                .connectTimeout(Duration.ofSeconds(6))
                .readTimeout(Duration.ofSeconds(6))
                .writeTimeout(Duration.ofSeconds(6))
                .execute()
                .asString();
        return res;
    }

    private static String getUseage(String key, String host,Proxy proxy) {
        DateTime start = DateUtil.beginOfMonth(new Date());
        DateTime end = DateUtil.offsetDay(new Date(), 1);
        String res2 = HttpRequest.get(host + URL_USEAGE)
                .setHeader("Authorization", "Bearer " + key)
                .setHeader("Content-type", "application/json")
                .query("start_date", DateUtils.parseDateToStr("yyyy-MM-dd",start))
                .query("end_date", DateUtils.parseDateToStr("yyyy-MM-dd",end))
                .proxy(proxy)
                .connectTimeout(Duration.ofSeconds(6))
                .readTimeout(Duration.ofSeconds(6))
                .writeTimeout(Duration.ofSeconds(6))
                .execute()
                .asString();
        return res2;
    }
}
