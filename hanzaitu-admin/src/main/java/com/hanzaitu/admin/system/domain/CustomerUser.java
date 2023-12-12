package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author cr
 * @since 2023-05-27
 */
@Getter
@Setter
@TableName("customer_user")
@ApiModel(value = "CustomerUser对象", description = "用户信息表")
public class CustomerUser implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户账号")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("用户昵称")
    @TableField("nick_name")
    private String nickName;

    @ApiModelProperty("用户邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("手机号码")
    @TableField("phone_number")
    private String phoneNumber;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("邀请链接")
    @TableField("invite_url")
    private String inviteUrl;

    @ApiModelProperty("帐号状态（0正常 1停用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("删除标志（0代表存在 2代表删除）")
    @TableField("del_flag")
    private Integer delFlag;

    @ApiModelProperty("最后登录IP")
    @TableField("login_ip")
    private String loginIp;

    @ApiModelProperty("最后登录时间")
    @TableField("login_date")
    private LocalDateTime loginDate;

    @ApiModelProperty("创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @ApiModelProperty("更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("我的邀请码")
    @TableField("invite_code")
    private String inviteCode;

    @ApiModelProperty("邀请人")
    @TableField("invite_user_id")
    private Long inviteUserId;

    @ApiModelProperty("头像")
    @TableField("avatar")
    private String avatar;
}
