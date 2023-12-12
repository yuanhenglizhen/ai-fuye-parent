package com.hanzaitu.ai.chat.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class ChatRecordEntity {

    private BigInteger id;

    private String winId;

    private Integer userId;

    private Integer sort;

    private String content;

    private String occurTime;

    private String userName;

    private String role;

    private String name;

    private String msgId;

    private BigInteger pid;

    private String expiration;

    private String model;

    private String subModel;
}
