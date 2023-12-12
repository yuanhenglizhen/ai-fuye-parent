package com.hanzaitu.common.core.session;

import java.util.Set;

public interface UserSession {


    /**
     * 获取用户id
     * @return
     */
    Long getUserId();

    /**
     * 获取用户登录名
     * @return
     */
    String getUserName();

    /**
     * 获取用户姓名
     * @return
     */
    String getNickName();

    /**
     * 获取手机号
     * @return
     */
    String getPhoneNumber();
    /**
     * 用户token
     * @return
     */
    String getAccessToken();

    /**
     * 获取当前登录域名
     * @return
     */
    String getOperationDomain();


    /**
     * 设置当前登录域名
     * @return
     */
    void setOperationDomain(String domain);


    /**
     * 系统账号
     */
    Long SYSTEM_USER_ID = 2L;

    /**
     * 系统登录名
     */
    String SYSTEM_USER_NAME = "system";

    /**
     * 系统账号名称
     */
    String SYSTEM_REAL_NAME = "系统";
}
