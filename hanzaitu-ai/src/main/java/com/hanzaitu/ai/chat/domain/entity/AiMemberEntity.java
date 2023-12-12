package com.hanzaitu.ai.chat.domain.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AiMemberEntity {
    private Integer id;

    private String memberName;

    private String password;

    private Integer status;

    private Integer sex;

    private String avatar;

    private String phone;

    private String email;

    private BigInteger integral;

    private String createTime;

    private String latestLoginTime;

    private String loginIp;

    private String updateTime;

    private Integer delFlag;

    private String userName;

}
