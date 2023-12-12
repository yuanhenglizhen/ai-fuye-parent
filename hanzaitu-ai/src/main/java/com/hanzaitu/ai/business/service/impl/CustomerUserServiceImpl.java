package com.hanzaitu.ai.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.business.domain.CustomerUser;
import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.ai.business.mapper.CustomerUserMapper;
import com.hanzaitu.ai.business.param.CustomerUserSaveParam;
import com.hanzaitu.ai.business.service.CustomerUserService;
import com.hanzaitu.ai.business.vo.CustomerUserVO;
import com.hanzaitu.common.config.HanZaiTuConfig;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.core.redis.RedisUtil;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.FinanceUserWallet;
import com.hanzaitu.common.mapper.FinanceUserWalletMapper;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.DefaultPointConfig;
import com.hanzaitu.common.service.ISysConfigService;
import com.hanzaitu.common.utils.Assert;
import com.hanzaitu.common.utils.bean.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author cr
 * @since 2023-05-27
 */
@Slf4j
@Service
public class CustomerUserServiceImpl extends HztBaseService implements CustomerUserService {

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private FinanceUserWalletMapper financeUserWalletMapper;

    @Autowired
    private ISysConfigService sysConfigService;
    @Override
    public List<CustomerUser> getList() {
        UserThreadLocal.get().getUserId().toString();
        return customerUserMapper.selectAll();
    }

    @Override
    public CustomerUserVO getUserInfo() {
        CustomerUser customerUser = customerUserMapper.selectById(UserThreadLocal.get().getUserId());
        CustomerUserVO vo = BeanUtils.copyProperties(customerUser, CustomerUserVO.class);

        FinanceUserWallet wallet = financeUserWalletMapper.selectOne(FinanceUserWallet::getCustomerUserId, customerUser.getId());
        vo.setPoints(wallet.getPoints());
        Long count = customerUserMapper.selectCount(CustomerUser::getInviteUserId, customerUser.getId());
        vo.setInviteCount(count);
        return vo;
    }

    @Override
    public CustomerUserVO editUser(CustomerUserSaveParam param) {
        CustomerUser customerUser = customerUserMapper.selectById(UserThreadLocal.get().getUserId());
        customerUser.setNickName(param.getNickName());
        customerUserMapper.updateById(customerUser);
        return getUserInfo();
    }

    @Override
    public CustomerUserVO editPhoneNumber(CustomerUserSaveParam param) {
        Assert.notBlank(param.getPhoneNumber(),"手机号不能为空");
        Assert.notBlank(param.getVerificationCode(),"验证码不能为空");
        CustomerUser customerUser = customerUserMapper.selectOne(CustomerUser::getPhoneNumber,
                param.getPhoneNumber(), CustomerUser::getDelFlag, 0);
        if (Objects.nonNull(customerUser)) {
            throw ResultException.createResultException("手机号已存在，无法绑定");
        }
        String realCode = (String) RedisUtil.get(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getPhoneNumber());
        if (!param.getVerificationCode().equals(realCode)){
            throw ResultException.createResultException("验证码错误");
        }

        customerUser = new CustomerUser();
        customerUser.setId(UserThreadLocal.get().getUserId());
        customerUser.setPhoneNumber(param.getPhoneNumber());
        customerUserMapper.updateById(customerUser);
        return getUserInfo();
    }

    @Override
    public CustomerUserVO editAvatar(CustomerUserSaveParam param) {
        CustomerUser customerUser = customerUserMapper.selectById(UserThreadLocal.get().getUserId());
        customerUser.setAvatar(param.getAvatar());
        customerUserMapper.updateById(customerUser);
        return getUserInfo();
    }

    @Override
    public HztPage<CustomerUserVO> listInviteUser(SearchListDto searchListDto) {
        QueryWrapper queryWrapper = new QueryWrapper<CustomerUser>();
        queryWrapper.eq("invite_user_id",UserThreadLocal.get().getUserId());
        if (searchListDto.getStartTime() != null && searchListDto.getEndTime() != null) {
            queryWrapper.between("create_time",searchListDto.getStartTime(),searchListDto.getEndTime());
        }
        HztPage<CustomerUser> customerUsers = customerUserMapper.selectPage(searchListDto,queryWrapper);
        DefaultPointConfig config = sysConfigService.getDefaultPointsConfig();
        Integer invitePoints;
        if (Objects.nonNull(config)) {
            invitePoints = config.getInvitePoints();
        } else {
            invitePoints = 20;
        }
        HztPage<CustomerUserVO>  page = customerUsers.convert(CustomerUserVO.class);
        page.getList().stream().forEach(vo -> {
            vo.setPoints(invitePoints);
        });
        return page;
    }
}