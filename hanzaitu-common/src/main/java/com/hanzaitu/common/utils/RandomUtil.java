package com.hanzaitu.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private static final ThreadLocalRandom random=ThreadLocalRandom.current();

    /**
     * 生成随机-方式一(时间+4位随机数)
     * @return
     */
    public static String generateOrderCode(){
        return  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + generateNumber(4);
    }

    /**
     * 生成随机-方式二(时间+ num 位随机数)
     * @return
     */
    public static String generateOrderCode(final int num){
        return  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + generateNumber(num);
    }

    /**
     * 生成随机-方式三(num 位随机数)
     * @return
     */
    public static String generateNumber(final int num){
        StringBuffer sb=new StringBuffer();
        for (int i=1;i<=num;i++){
            sb.append(random.nextInt(9));
        }
        return sb.toString();

    }

    /**
     * 随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length){
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
