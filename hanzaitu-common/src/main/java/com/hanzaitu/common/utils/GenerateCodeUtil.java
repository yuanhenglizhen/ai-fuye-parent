package com.hanzaitu.common.utils;

import java.util.Random;

public class GenerateCodeUtil {

    public static String generateCode(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numList = "0123456789";
        String rev = "";
        int maxNumCount = 4;
        Random f = new Random();
        for (int i = 0; i < length; i++) {
            if (f.nextBoolean() && maxNumCount > 0) {
                maxNumCount --;
                rev += numList.charAt(f.nextInt(numList.length()));
            } else {
                rev += chars.charAt(f.nextInt(chars.length()));
            }
        }
        return rev;
    }
}
