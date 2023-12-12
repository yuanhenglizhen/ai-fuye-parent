package com.hanzaitu.ai.draw.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("提交图生文任务参数")
public class DescribeDTO {
	/**
	 * 自定义参数.
	 */
	@ApiModelProperty(value = "自定义参数")
	private String state;

	/**
	 * 图片路径
	 */
	@NotBlank(message = "图片路径不能为空")
	@ApiModelProperty(value = "图片路径", required = true)
	private String imgUrl;

	/**
	 * notifyHook of caller.
	 */
	@ApiModelProperty("回调地址")
	private String notifyHook;
}
