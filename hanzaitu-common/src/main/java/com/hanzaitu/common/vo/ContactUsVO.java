package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 联系我们
 */
@Data
public class ContactUsVO {

    @ApiModelProperty("主图片")
    private String mainImg;

    @ApiModelProperty("主图片")
    private String mainImgMessage;

    @ApiModelProperty("微信二维码")
    private String wxImg;

    @ApiModelProperty("主图片")
    private String wxImgMessage;

    @ApiModelProperty("加入创作者论坛")
    private String joinImg;

    @ApiModelProperty("主图片")
    private String joinImgMessage;

    @ApiModelProperty("关注抖音")
    private String dy1Img;

    @ApiModelProperty("主图片")
    private String dy1ImgMessage;

    @ApiModelProperty("关注抖音")
    private String dy2Img;

    @ApiModelProperty("主图片")
    private String dy2ImgMessage;
}
