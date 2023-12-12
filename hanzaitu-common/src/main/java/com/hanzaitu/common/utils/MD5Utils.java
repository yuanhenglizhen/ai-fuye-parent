package com.hanzaitu.common.utils;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;
public class MD5Utils {


    /**
     * 使用tyche自己定义MD5
     */
    public static String doMD5Sign(String targetStr) {

        byte[] md5Result = new byte[0];
        try {
            md5Result = MessageDigest.getInstance("MD5").digest(targetStr.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (md5Result.length != 16) {
            throw new IllegalArgumentException("MD5加密结果字节数组错误");
        }
        int first = Math.abs(bytesToInt(md5Result, 0));
        int second = Math.abs(bytesToInt(md5Result, 4));
        int third = Math.abs(bytesToInt(md5Result, 8));
        int fourth = Math.abs(bytesToInt(md5Result, 12));
        return String.format(Locale.CHINA, "%d%d%d%d", first, second, third, fourth);
    }

    private static int bytesToInt(byte[] src, int offset) {
        int value;
        value = ((src[offset] & 0xFF) << 24) | ((src[offset + 1] & 0xFF) << 16) | ((src[offset + 2] & 0xFF) << 8)
            | (src[offset + 3] & 0xFF);
        return value;
    }

    public static String encrypt(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes(Charset.forName("utf-8")));
            return toHex(digest.digest());
        } catch (Exception e) {
            return null;
        }
    }

    public static String encrypt(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            return toHex(digest.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            buffer.append(Character.forDigit((bytes[i] & 240) >> 4, 16));
            buffer.append(Character.forDigit(bytes[i] & 15, 16));
        }
        return buffer.toString();
    }

    /**
     * 获取MD5的密码字符串
     *
     * @param salt
     * @param pwd
     * @return
     */
    public static String getMD5Pwd(final String salt, final String pwd) {
        return DigestUtils.md5Hex((salt + pwd).getBytes(Charsets.UTF_8)).toUpperCase();
    }

    public static String encodeStr(final String str) {
        String timeStr = DateFormatUtils.format(new Date(), "yyyyMMdd HH:mm:ss");
        return DigestUtils.md5Hex((str+timeStr).getBytes(Charsets.UTF_8)).toUpperCase();
    }

    public static void main(String[] args) {
        String key = "123456789";
        System.out.println("TAGE" + encrypt(key));

    }
}
