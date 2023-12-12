package com.hanzaitu.common.domain;

    import com.baomidou.mybatisplus.annotation.FieldFill;
    import com.baomidou.mybatisplus.annotation.FieldStrategy;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.baomidou.mybatisplus.annotation.TableName;
    import java.io.Serializable;
    import java.time.LocalDateTime;
    import io.swagger.annotations.ApiModel;
    import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
* <p>
    * 用户财产积分记录表
    * </p>
*
* @author cr
* @since 2023-06-03
*/
@Getter
@Setter
    @TableName("finance_user_wallet")
    @ApiModel(value = "FinanceUserRecord对象", description = "用户财产积分记录表")
public class FinanceUserWallet implements Serializable {

private static final long serialVersionUID = 1L;


    private Long id;

    @ApiModelProperty("用户id")
    @TableField("customer_user_id")
    private Long customerUserId;


    @ApiModelProperty("积分")
    @TableField("points")
    private Integer points = 0;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT , updateStrategy= FieldStrategy.NEVER)
    private LocalDateTime createTime;


    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
