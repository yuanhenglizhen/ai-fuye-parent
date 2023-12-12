package com.hanzaitu.admin.system.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@TableName("ai_chat_expired_config")
public class AiChatExpiredConfig implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("保留天数")
    @NotNull(message = "保留天数不能为null】")
    @TableField("chat_expired_day")
    private Integer  chatExpiredDay;

    @ApiModelProperty("0 开启 1 关闭")
    @NotNull(message = "是否开启不能为空")
    @TableField("if_on")
    private String ifOn;
}
