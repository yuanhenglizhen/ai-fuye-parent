package com.hanzaitu.ai.chat.domain.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanzaitu.common.enums.GptKeyTypeEnum;
import lombok.Data;

@Data
@TableName("ai_gpt_model")
public class AiGptModelEntity {

   @TableField("model")
   private String model;

   @JsonIgnore
   @TableField("is_on")
   private Integer isOn;


   @TableField("type")
   private GptKeyTypeEnum type;
}
