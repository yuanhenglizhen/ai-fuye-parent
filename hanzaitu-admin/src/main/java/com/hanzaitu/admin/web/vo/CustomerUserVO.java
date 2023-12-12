package com.hanzaitu.admin.web.vo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerUserVO {


    @ApiModelProperty("用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户账号")
    private String userName;

    @ApiModelProperty("手机号码")
    private String phoneNumber;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("邀请链接")
    private String inviteUrl;

    @ApiModelProperty("帐号状态（0正常 1停用）")
    private Integer status;


    @ApiModelProperty("最后登录IP")
    private String loginIp;

    @ApiModelProperty("最后登录时间")
    private LocalDateTime loginDate;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("我的邀请码")
    private String inviteCode;

    @ApiModelProperty("邀请人")
    private Long inviteUserId;

    @ApiModelProperty("个人积分")
    private Integer points;
}
