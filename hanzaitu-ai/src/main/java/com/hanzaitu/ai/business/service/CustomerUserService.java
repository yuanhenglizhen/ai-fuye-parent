package com.hanzaitu.ai.business.service;

import com.hanzaitu.ai.business.domain.CustomerUser;
import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.ai.business.param.CustomerUserSaveParam;
import com.hanzaitu.ai.business.vo.CustomerUserVO;
import com.hanzaitu.common.page.HztPage;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author cr
 * @since 2023-05-27
 */
public interface CustomerUserService  {

    List<CustomerUser> getList();

    /**
     * 获取用户信息
     * @return
     */
    CustomerUserVO getUserInfo();

    /**
     * 编辑用户信息
     * @param param
     * @return
     */
    CustomerUserVO editUser(CustomerUserSaveParam param);

    /**
     * 编辑手机号
     * @param param
     * @return
     */
    CustomerUserVO editPhoneNumber(CustomerUserSaveParam param);

    /**
     * 编辑手机号
     * @param param
     * @return
     */
    CustomerUserVO editAvatar(CustomerUserSaveParam param);
    /**
     * 获取用户邀请明细信息
     * @return
     */
    HztPage<CustomerUserVO> listInviteUser(SearchListDto searchListDto);
}