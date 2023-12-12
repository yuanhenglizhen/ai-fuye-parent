package com.hanzaitu.common.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.hanzaitu.common.constant.Constants;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.core.redis.RedisUtil;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.FinanceUserWallet;
import com.hanzaitu.common.enums.NotifyType;
import com.hanzaitu.common.enums.RecordTypeEnum;
import com.hanzaitu.common.enums.UserNotifyEnum;
import com.hanzaitu.common.mapper.FinanceUserWalletMapper;
import com.hanzaitu.common.params.ExpendConfigParam;
import com.hanzaitu.common.params.FinanceUserWalletRecordSaveParam;
import com.hanzaitu.common.params.WalletPointParam;
import com.hanzaitu.common.service.FinanceUserWalletRecordService;
import com.hanzaitu.common.service.FinanceUserWalletService;
import com.hanzaitu.common.service.ISysConfigService;
import com.hanzaitu.common.service.NotifyMsgCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户财产积分记录表 服务实现类
 * </p>
 *
 * @author cr
 * @since 2023-06-03
 */
@Service
public class FinanceUserWalletServiceImpl extends HztBaseService implements FinanceUserWalletService {

    @Autowired
    private FinanceUserWalletMapper financeUserWalletMapper;

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    FinanceUserWalletRecordService financeUserWalletRecordService;

    @Autowired
    NotifyMsgCommonService notifyMsgCommonService;


    @Override
    public void updateWalletPoints(WalletPointParam param) {
        if (RedisUtil.hasKey(Constants.WALLER_KEY +param.getUserId())) {
            throw ResultException.createResultException("积分正在使用中请重试！");
        }
        RedisUtil.set(Constants.WALLER_KEY +param.getUserId(),param.getPoints(),2L, TimeUnit.MINUTES);
        FinanceUserWallet wallet = financeUserWalletMapper.selectOne(FinanceUserWallet::getCustomerUserId, param.getUserId());
        Integer points = wallet.getPoints()+param.getPoints();
        if (points < 0) {
            RedisUtil.del(Constants.WALLER_KEY +param.getUserId());
            throw ResultException.createResultException("积分不足！无法使用");
        }
        wallet.setPoints(points);
        financeUserWalletMapper.updateById(wallet);
        RedisUtil.del(Constants.WALLER_KEY +param.getUserId());
    }

    @Override
    public void initUserWallet(Long customerUserId, Integer initPoints) {
        FinanceUserWallet wallet = financeUserWalletMapper.selectOne(FinanceUserWallet::getCustomerUserId, customerUserId);
        if (Objects.nonNull(wallet)) {
            return ;
        }
        wallet = new FinanceUserWallet();
        wallet.setPoints(initPoints);
        wallet.setCustomerUserId(customerUserId);
        financeUserWalletMapper.insert(wallet);
    }

    /**
     * 积分消耗
     * @param typeEnum
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletPointParam expendGptPoints(RecordTypeEnum typeEnum) {
        if (!RedisUtil.hasKey(Constants.GPT_POINT_KEY)) {
            RedisUtil.set(Constants.GPT_POINT_KEY, JSONUtil.toJsonStr(sysConfigService.listExpendConfig()));
        }
        JSON.parseArray(RedisUtil.get(Constants.GPT_POINT_KEY).toString(), ExpendConfigParam.class);
        List<ExpendConfigParam> expendConfigParamList = JSON.parseArray(RedisUtil.get(Constants.GPT_POINT_KEY).toString(),
                ExpendConfigParam.class);
        if (CollectionUtils.isEmpty(expendConfigParamList)) {
            throw ResultException.createResultException("积分消耗配置不存在，无法使用积分");
        }
        Integer points = 0;
        for (ExpendConfigParam config: expendConfigParamList) {
            if (typeEnum.getValue().equals(config.getExpendType())) {
                points = config.getPoints();
            }
        }
        if (points == 0) {
            throw ResultException.createResultException("积分消耗配置不存在，无法使用积分");
        }
        FinanceUserWalletRecordSaveParam saveParam = new FinanceUserWalletRecordSaveParam();
        saveParam.setCustomerUserId(UserThreadLocal.get().getUserId());
        saveParam.setPoints(-points);
        saveParam.setRecordType(typeEnum.getValue());
        financeUserWalletRecordService.saveWalletRecord(saveParam);
        WalletPointParam walletPointParam = new WalletPointParam();
        walletPointParam.setPoints(saveParam.getPoints());
        walletPointParam.setUserId(saveParam.getCustomerUserId());
        this.updateWalletPoints(walletPointParam);
        return walletPointParam;
    }

    /**
     * 积分返还
     * @param typeEnum
     * @param points
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnGptPoints(RecordTypeEnum typeEnum, Integer points, Long userId) {
        FinanceUserWalletRecordSaveParam saveParam = new FinanceUserWalletRecordSaveParam();
        saveParam.setCustomerUserId(userId);
        saveParam.setPoints(points);
        saveParam.setRecordType(typeEnum.getValue());
        financeUserWalletRecordService.saveWalletRecord(saveParam);
        WalletPointParam walletPointParam = new WalletPointParam();
        walletPointParam.setPoints(saveParam.getPoints());
        walletPointParam.setUserId(saveParam.getCustomerUserId());
        this.updateWalletPoints(walletPointParam);
        notifyMsgCommonService.publishUserMessage(userId,String.format(UserNotifyEnum.REBATE_POINTS.getMsg(),typeEnum.getDescription()));
    }


}