package com.hanzaitu.ai.business.vo;

import com.hanzaitu.common.enums.NotifyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserNotifyVo对象", description = "用户通知")
public class NotifyVo {

    @ApiModelProperty("消息Id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("GLOBAL 全局系统消息  USER  用户个人消息")
    private NotifyType type;

    @ApiModelProperty("创建日期")
    private String createTime;
}
