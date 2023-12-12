package com.hanzaitu.ai.business.vo;


import com.hanzaitu.ai.business.domain.HomeConfig;
import com.hanzaitu.ai.business.domain.SysConfigPayType;
import com.hanzaitu.ai.chat.domain.entity.AiChatExpiredConfigEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("配置信息实体")
@Data
public class ConfigInfoVo {

    @ApiModelProperty("支持支付列表")
    private SysConfigPayType payType;

    @ApiModelProperty("聊天保留时间")
    private AiChatExpiredConfigEntity expiredConfig;

    @ApiModelProperty("首页配置")
    private HomeConfig homeConfig;

}

