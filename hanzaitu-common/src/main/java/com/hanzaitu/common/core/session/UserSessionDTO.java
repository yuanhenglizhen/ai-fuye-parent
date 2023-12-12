package com.hanzaitu.common.core.session;

import lombok.Data;

import java.util.Set;

@Data
public class UserSessionDTO implements UserSession{

    private static final long serialVersionUID = -5831179721009961140L;

    /**
     * 用户Id
     */
    private Long id;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户姓名
     */
    private String nickName;

    /**
     * token
     */
    private String accessToken;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 操作域名
     */
    private String operationDomain;


    @Override
    public Long getUserId() {
        return id;
    }
    /**
     * 获取用户登录名
     *
     * @return
     */
    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * 用户session是否初始化
     */
    private boolean init;
}
