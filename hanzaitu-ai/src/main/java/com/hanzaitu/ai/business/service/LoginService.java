package com.hanzaitu.ai.business.service;

import com.hanzaitu.ai.business.param.CustomerUserSaveMailParam;
import com.hanzaitu.ai.business.param.CustomerUserSaveParam;
import com.hanzaitu.ai.business.param.LoginParam;
import com.hanzaitu.ai.business.param.LoginPhoneParam;
import com.hanzaitu.ai.business.vo.CustomerUserVO;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.session.UserSessionDTO;

public interface LoginService {

    /**
     * 用户登录
     * @param loginParam
     * @return
     */
    UserSessionDTO login(LoginParam loginParam);


    /**
     * 手机登录
     * @param loginParam
     * @return
     */
    UserSessionDTO loginPhoneNumber(LoginPhoneParam loginParam);

    /**
     * 登出
     * @param accessToken
     */
    void logout(String accessToken);

    /**
     * 注册
     * @param param
     * @return customerUserVO 返回
     */
    boolean register(CustomerUserSaveParam param);

    /**
     * 注册
     * @param param
     * @return customerUserVO 返回
     */
    boolean registerEmail(CustomerUserSaveMailParam param);

    /**
     * 发送验证码
     * @param phoneNumber
     */
    void sendRegisterCode(String phoneNumber);

    /**
     * 发送验证码
     * @param email
     */
    AjaxResult sendMailRegisterCode(String email);

    /**
     * 发送验证码
     * @param phoneNumber
     */
    void resetPassword(CustomerUserSaveParam param);

    /**
     * 发送验证码
     * @param phoneNumber
     */
    void resetPasswordEmail(CustomerUserSaveMailParam param);
}
