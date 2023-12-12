package com.hanzaitu.common.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
* <p>
    * 用户财产积分钱包记录表
    * </p>
*
* @author cr
* @since 2023-06-04
*/
@Getter
@Setter
    @TableName("finance_user_wallet_record")
    @ApiModel(value = "FinanceUserWalletRecord对象", description = "用户财产积分钱包记录表")
public class FinanceUserWalletRecord implements Serializable {

private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("customer_user_id")
    private Long customerUserId;

    @ApiModelProperty("积分")
    @TableField("points")
    private Integer points;

    @ApiModelProperty("变更类型")
    @TableField("record_type")
    private String recordType;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT , updateStrategy= FieldStrategy.NEVER)
    private LocalDateTime createTime;

}
