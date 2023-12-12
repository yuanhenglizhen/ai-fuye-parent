package com.hanzaitu.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.core.mybatis.MybatisPage;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.FinanceUserWalletRecord;
import com.hanzaitu.common.domain.SearchListDto;
import com.hanzaitu.common.enums.RecordTypeEnum;
import com.hanzaitu.common.mapper.FinanceUserWalletRecordMapper;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.FinanceUserWalletRecordQueryParam;
import com.hanzaitu.common.params.FinanceUserWalletRecordSaveParam;
import com.hanzaitu.common.service.FinanceUserWalletRecordService;
import com.hanzaitu.common.utils.bean.BeanUtils;
import com.hanzaitu.common.vo.FinanceUserWalletRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户财产积分钱包记录表 服务实现类
 * </p>
 *
 * @author cr
 * @since 2023-06-04
 */
@Service
public class FinanceUserWalletRecordServiceImpl extends HztBaseService implements FinanceUserWalletRecordService {

    @Autowired
    private FinanceUserWalletRecordMapper financeUserWalletRecordMapper;
    @Override
    public void saveWalletRecord(FinanceUserWalletRecordSaveParam param) {
        FinanceUserWalletRecord record = BeanUtils.copyProperties(param, FinanceUserWalletRecord.class);
        financeUserWalletRecordMapper.insert(record);
    }


    @Override
    public HztPage<FinanceUserWalletRecordVO> listUserWalletRecord(FinanceUserWalletRecordQueryParam param) {
        param.setUserId(UserThreadLocal.get().getUserId());
        HztPage<FinanceUserWalletRecordVO> records = financeUserWalletRecordMapper.selectListByCustomerUserId(MybatisPage.convert(param), param).convert();
        records.getList().stream().forEach(record -> {
            record.setRecordTypeName(RecordTypeEnum.getDescriptionByValue(record.getRecordType()));
        });
        return records;
    }
}