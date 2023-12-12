package com.hanzaitu.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hanzaitu.common.config.KD100Config;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.enums.SmsEnum;
import com.hanzaitu.common.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SmsUtils {


    /**
     * 快递100短信验证码接口
     *
     * @param config 快递100配置类
     * @param phone  电话
     * @param code   验证码
     */
    public static void sendKD100Sms(KD100Config config, String phone, String code) {
        ObjectNode node = new ObjectMapper().createObjectNode().put("code", code);
        String content = node.toPrettyString();
        smsCommon(config, phone, config.getTid(), content);
    }

    public static void smsCommon(KD100Config config, String phone, String tid, String content) {
        String sign = DigestUtils.md5Hex(config.getKey() + config.getUserid()).toUpperCase();
        Map<String, String> params = new HashMap<>();
        params.put("sign", sign);
        params.put("userid", config.getUserid());
        params.put("seller", config.getSeller());
        params.put("phone", phone);
        params.put("tid", tid);
        params.put("content", content);
        String result = HttpUtils.sendPost(config.getUrl(), params, null);
        JsonNode data;
        try {
            data = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            log.error("短信接口异常", e);
            throw ResultException.createResultException("短信发送失败");
        }
        if (1 != data.get("status").asInt()) {
            log.error("短信接口异常:" + data.get("msg").toString());
            throw ResultException.createResultException("短信发送失败");
        }
    }
}
