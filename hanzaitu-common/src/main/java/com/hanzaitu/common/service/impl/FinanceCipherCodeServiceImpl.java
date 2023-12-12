package com.hanzaitu.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.hanzaitu.common.constant.UserConstants;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.core.mybatis.HztLambdaQueryChainWrapper;
import com.hanzaitu.common.domain.FinanceCipherCode;
import com.hanzaitu.common.enums.CipherStatus;
import com.hanzaitu.common.mapper.FinanceCipherCodeMapper;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.FinanceCipherCodeParam;
import com.hanzaitu.common.service.FinanceCipherCodeService;
import com.hanzaitu.common.utils.GenerateCodeUtil;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.bean.BeanUtils;
import com.hanzaitu.common.vo.FinanceCipherCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 卡密表 服务实现类
 * </p>
 *
 * @author cr
 * @since 2023-06-11
 */
@Service
public class FinanceCipherCodeServiceImpl extends HztBaseService implements FinanceCipherCodeService {

    @Autowired
    private FinanceCipherCodeMapper financeCipherCodeMapper;

    @Override
    public List<String> generateCipherCode(Integer count) {
        List<String> codes = new ArrayList<>();
        while (true) {
            String code = GenerateCodeUtil.generateCode(UserConstants.CIPHER_CODE_LENGTH);
            FinanceCipherCode cipherCode = this.getCipherCodeByCode(code);
            if (Objects.nonNull(cipherCode)) {
                continue;
            }
            codes.add(code);
            if (codes.size() >= count) {
                break;
            }
        }
        return codes;
    }

    @Override
    public void insertBatch(List<FinanceCipherCode> cipherCodes) {
        financeCipherCodeMapper.insertBatchSomeColumn(cipherCodes);
    }

    @Override
    public HztPage<FinanceCipherCodeVO> listCipherCode(FinanceCipherCodeParam param) {
        HztLambdaQueryChainWrapper<FinanceCipherCode> queryChainWrapper = financeCipherCodeMapper.hztLambdaQuery();
        queryChainWrapper.eqNotEmpty(FinanceCipherCode::getCipherCode, param.getCipherCode()).eqNotEmpty(FinanceCipherCode::getStatus, param.getStatus());
        HztPage<FinanceCipherCode> page = financeCipherCodeMapper.selectPage(param, queryChainWrapper.getWrapper());
        List<FinanceCipherCodeVO> vos = BeanUtil.copyToList(page.getList(), FinanceCipherCodeVO.class);
        vos.stream().forEach(vo -> {
            vo.setStatusName(CipherStatus.getDescriptionByValue(vo.getStatus()));
        });
        HztPage<FinanceCipherCodeVO> pageVO = page.convert(FinanceCipherCodeVO.class);
        pageVO.setList(vos);
        return pageVO;
    }

    @Override
    public FinanceCipherCode getCipherCodeByCode(String code) {
        FinanceCipherCode financeCipherCode = financeCipherCodeMapper.selectOne(FinanceCipherCode::getCipherCode, code);
        return financeCipherCode;
    }
}