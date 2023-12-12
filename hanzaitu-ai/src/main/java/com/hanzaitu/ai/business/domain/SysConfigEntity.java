package com.hanzaitu.ai.business.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hanzaitu.common.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 参数配置表 sys_config
 * 
 * @author ruoyi
 */
@Data
@TableName("sys_config")
public class SysConfigEntity
{
    private static final long serialVersionUID = 1L;

    /** 参数主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 参数名称 */
    @TableField("config_name")
    private String configName;

    /** 参数键名 */
    @TableField("config_key")
    private String configKey;

    /** 参数键值 */
    @TableField("config_value")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @TableField("config_type")
    private String configType;

    /** 创建者 */
    @TableField("create_by")
    private String createBy;


    /** 备注 */
    @TableField("remark")
    private String remark;
}
