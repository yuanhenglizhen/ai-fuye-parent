package com.hanzaitu.ai.business.mapper;

import com.hanzaitu.ai.business.domain.SysConfigEntity;
import com.hanzaitu.common.core.HztBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysConfigInfoMapper extends HztBaseMapper<SysConfigEntity> {

    List<SysConfigEntity> selectSysConfigList(@Param("configKeyList") List<String> configKeyList);

}
