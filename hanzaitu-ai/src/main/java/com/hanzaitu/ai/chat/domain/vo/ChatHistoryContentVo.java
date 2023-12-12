package com.hanzaitu.ai.chat.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("聊天内容")
@Data
public class ChatHistoryContentVo {
    /**
     * 窗口id
     */
    @ApiModelProperty("窗口id")
    private String winId;


    /**
     * 消息Id
     */
    @ApiModelProperty("消息Id")
    private String msgId;

    /**
     * 收藏Id
     */
    @ApiModelProperty("收藏Id")
    private String collectId;

    /**
     * 文本内容
     */
    @ApiModelProperty("文本内容")
    private String content;

    /**
     * 发生时间
     */
    @ApiModelProperty("发生时间")
    private String occurTime;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    /**
     * 角色 user用户 assistant chat助手
     */
    @ApiModelProperty("角色 user用户 assistant chat助手")
    private String role;


    /**
     * 子模型
     */
    @ApiModelProperty("子模型")
    private String subModel;
}
