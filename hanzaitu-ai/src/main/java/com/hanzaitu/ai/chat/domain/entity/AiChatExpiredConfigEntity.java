package com.hanzaitu.ai.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@TableName("ai_chat_expired_config")
public class AiChatExpiredConfigEntity {

    @TableField("chat_expired_day")
    private Integer chatExpiredDay;

    @TableField("if_on")
    private Integer ifOn;

}
