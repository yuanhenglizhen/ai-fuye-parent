package com.hanzaitu.ai.chat.domain.entity;

import lombok.Data;

@Data
public class WindowInfoEntity {


    private long expiredTime;


    private long userId;


    private String model;

}
