package com.hanzaitu.admin.system.domain.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class AiHomeBannerDto {

    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 连接
     */
    private String href;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 存储方式
     */
    private Integer storageType;

}
