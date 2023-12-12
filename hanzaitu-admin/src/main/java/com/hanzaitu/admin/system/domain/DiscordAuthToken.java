package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@TableName("ai_draw_key")
public class DiscordAuthToken {

    @TableId(value = "token_id", type = IdType.AUTO)
    private Integer tokenId;

    /**
     * discord服务器ID
     */
    @ApiModelProperty("服务器Id")
    @NotBlank(message = "服务器Id名称不能为空")
    @TableField("guild_id")
    private String guildId;

    /**
     * discord频道ID
     */
    @ApiModelProperty("频道ID")
    @NotBlank(message = "频道名称不能为空")
    @TableField("channel_id")
    private String channelId;

    /**
     * discord用户Token
     */
    @ApiModelProperty("用户token")
    @NotBlank(message = "用户token不能为空")
    @TableField("user_token")
    private String userToken;

    /**
     * 自定义机器人Token
     */
    @ApiModelProperty("机器人Token")
    @NotBlank(message = "机器人Token不能为空")
    @TableField("bot_token")
    private String botToken;

    /**
     * 并发数.
     */
    @ApiModelProperty("并发数")
    @NotNull(message = "并发数不能为空")
    @TableField("core_size")
    private Integer coreSize;

    /**
     * 创建时间
     */
    @ApiModelProperty("并发数")
    @TableField("create_time")
    private String createTime;

    /**
     * 代理地址
     */
    @TableField("proxy_host")
    private String proxyHost;

    /**
     * 代理端口
     */
    @TableField("proxy_port")
    private Integer proxyPort;
}
