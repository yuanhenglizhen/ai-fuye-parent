package com.hanzaitu.common.domain;

    import com.baomidou.mybatisplus.annotation.FieldFill;
    import com.baomidou.mybatisplus.annotation.FieldStrategy;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.baomidou.mybatisplus.annotation.TableName;
    import java.io.Serializable;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import io.swagger.annotations.ApiModel;
    import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
    import lombok.experimental.FieldNameConstants;


/**
* <p>
    * 卡密表
    * </p>
*
* @author cr
* @since 2023-06-11
*/
@Getter
@Setter
    @TableName("finance_cipher_code")
    @ApiModel(value = "FinanceCipherCode对象", description = "卡密表")
@FieldNameConstants
public class FinanceCipherCode implements Serializable {

private static final long serialVersionUID = 1L;


    private Long id;

            @ApiModelProperty("卡密金额")
        @TableField("total_amount")
    private BigDecimal totalAmount;

            @ApiModelProperty("卡密积分")
        @TableField("points")
    private Integer points;

            @ApiModelProperty("卡密")
        @TableField("cipher_code")
    private String cipherCode;

            @ApiModelProperty("状态")
        @TableField("status")
    private String status;

    @ApiModelProperty("使用用户")
    @TableField("use_user_id")
    private Long useUserId;

            @ApiModelProperty("创建时间")
            @TableField(value = "create_time", fill = FieldFill.INSERT , updateStrategy= FieldStrategy.NEVER)
    private LocalDateTime createTime;

            @ApiModelProperty("更新时间")
            @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE )
    private LocalDateTime updateTime;

}
