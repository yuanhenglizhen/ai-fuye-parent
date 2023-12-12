package com.hanzaitu.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.*;

public enum ExpendTypeEnum implements IEnum {

    GPT_35_TURBO("gpt-3.5-turbo","Ai聊天（gpt-3.5-turbo"),
    GPT_35_TURBO_0613("gpt-3.5-turbo-0613","Ai聊天（gpt-3.5-turbo-0613）"),
    GPT_35_TURBO_16K ("gpt-3.5-turbo-16k","Ai聊天（gpt-3.5-turbo-16k）"),
    GPT_35_TURBO_0301 ("gpt-3.5-turbo-0301","Ai聊天（gpt-3.5-turbo-0301）"),
    GPT_35_TURBO_16K_0613("gpt-3.5-turbo-16k-0613","Ai聊天（gpt-3.5-turb0-16k-0613）"),
    GPT_4 ("gpt-4","Ai聊天（gpt-4）"),
    GPT_4_0314 ("gpt-4-0314","Ai聊天（gpt-4-0314）"),
    GPT_4_0613  ("gpt-4-0613","Ai聊天（gpt-4-0613）"),
    GPT_4_32K("gpt-4-32k","Ai聊天（gpt-4-32k）"),
    GPT_4_32K_0613("gpt-4-32k-0613","Ai聊天（gpt-4-32k-0314）"),
    GPT_4_32K_0314 ("gpt-4-32k-0314","Ai聊天（gpt-4-32k-0613）"),
    MIDJOURNEY ("Midjourney","Ai绘画（Midjourney）"),
            ;

    private String value;
    private String description;

    private ExpendTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean eqValue(Integer value) {
        if (Objects.isNull(value)) {
            return false;
        } else {
            return Objects.isNull(this.value) ? false : value.equals(this.value);
        }
    }

    public static String getDescriptionByValue(String value) {
        ExpendTypeEnum[] values = ExpendTypeEnum.values();
        for (ExpendTypeEnum status: values) {
            if (value.equals(status.value)) {
                return status.description;
            }
        }
        return null;
    }

    public static List<Map<String, String>> enumToMap(){
        List<Map<String, String>> maps = new ArrayList<>();
        ExpendTypeEnum[] values = ExpendTypeEnum.values();
        for (ExpendTypeEnum status: values) {
            Map<String,String> map= new HashMap<>();
            map.put(status.getValue(), status.getDescription());
            maps.add(map);
        }
        return maps;
    }
}
