package com.hanzaitu.ai.draw.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DiscordAuthTokenEntity {


    @TableField(exist = false)
    private Integer tokenId;

    /**
     * discord服务器ID
     */
    @TableField(exist = false)
    @JsonIgnore
    private String guildId;

    /**
     * discord频道ID
     */
    @TableField(exist = false)
    //@JsonIgnore
    private String channelId;

    /**
     * discord用户Token
     */
    @TableField(exist = false)
    @JsonIgnore
    private String userToken;

    /**
     * 自定义机器人Token
     */
    @TableField(exist = false)
    @JsonIgnore
    private String botToken;

    /**
     * 并发数.
     */
    @TableField(exist = false)
    @JsonIgnore
    private Integer coreSize;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    @JsonIgnore
    private String createTime;

    /**
     * 代理地址
     */
    @TableField(exist = false)
    @JsonIgnore
    private String proxyHost;// = "127.0.0.1";

    /**
     * 代理端口
     */
    @TableField(exist = false)
    @JsonIgnore
    private Integer proxyPort;// = 10809;

    /**
     * 绘画任务执行超时时间(分钟数)，任务超过这个时间会失败
     */
    @TableField(exist = false)
    @JsonIgnore
    private Integer timeOut;
}
