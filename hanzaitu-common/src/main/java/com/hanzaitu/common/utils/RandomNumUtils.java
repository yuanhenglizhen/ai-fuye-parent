package com.hanzaitu.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.Random;

public class RandomNumUtils {


    /**
     * 生成平台订单编号
     *
     * @param prefix 平台类型
     * @return 平台订单编号
     */
    public static String generatorCode(String prefix) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = df.format(System.currentTimeMillis());
        Random r = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int random = r.nextInt(10);
            code.append(random);
        }
        return prefix + now + code;
    }

    /**
     * 获取6位验证码
     *
     * @return 验证码
     */
    public static String getVerificationCode() {
        return RandomStringUtils.randomNumeric(6);
    }

    /**
     * 获取12位随机数
     *
     * @return 验证码
     */
    public static String getRandomSeed() {
        return RandomStringUtils.randomNumeric(12);
    }
}
