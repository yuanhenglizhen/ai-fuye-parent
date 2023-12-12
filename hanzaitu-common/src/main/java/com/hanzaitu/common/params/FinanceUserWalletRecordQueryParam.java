package com.hanzaitu.common.params;

import com.hanzaitu.common.page.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户钱包记录查询参数
 */
@Data
public class FinanceUserWalletRecordQueryParam extends PageParam {

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
