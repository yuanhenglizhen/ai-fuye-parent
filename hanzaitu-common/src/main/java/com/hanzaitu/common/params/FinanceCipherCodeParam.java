package com.hanzaitu.common.params;


import com.hanzaitu.common.page.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 卡密参数
 */
@Data
public class FinanceCipherCodeParam extends PageParam {

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("卡密")
    @NotBlank(message = "卡密不能为空")
    private String cipherCode;
}
