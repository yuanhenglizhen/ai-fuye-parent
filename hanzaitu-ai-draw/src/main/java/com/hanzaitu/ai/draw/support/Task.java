package com.hanzaitu.ai.draw.support;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanzaitu.ai.draw.domain.entity.DiscordAuthTokenEntity;
import com.hanzaitu.ai.draw.enums.Action;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ai_draw_record")
@ApiModel("任务")
public class Task extends DiscordAuthTokenEntity implements Serializable {

	//@Serial
	private static final long serialVersionUID = -674915748204390789L;

	@TableField("type")
	private Action action;

	@TableField("id")
	@ApiModelProperty("任务ID")
	private String id;

	@TableField("task_id")
	@JsonIgnore
	private String taskId;

	@TableField("prompt")
	@ApiModelProperty("提示词")
	private String prompt;

	@TableField("prompt_en")
	@ApiModelProperty("提示词-英文")
	private String promptEn;

	@TableField("description")
	@ApiModelProperty("任务描述")
	private String description;

	@TableField(exist = false)
	@ApiModelProperty("自定义参数")
	private String state;

	@TableField("submit_time")
	@ApiModelProperty("提交时间")
	private String submitTime;

	@TableField("start_time")
	@ApiModelProperty("开始执行时间")
	private String startTime;

	@TableField("finish_time")
	@ApiModelProperty("结束时间")
	private String finishTime;

	@TableField(exist = false)
	@ApiModelProperty("图片url")
	private String imageUrl;

	@TableField(exist = false)
	@ApiModelProperty("加速图片名")
	private String newImageUrl;

	@TableField(exist = false)
	@ApiModelProperty("图片主域名")
	private String cdnHost;

	@TableField("status")
	private TaskStatus status = TaskStatus.NOT_START;

	@TableField("fail_reason")
	@ApiModelProperty("失败原因")
	private String failReason;

	@TableField("progress")
	@ApiModelProperty("当前进度")
	private Integer progress = 0;

	@TableField("user_id")
	@ApiModelProperty("用户Id")
	private Long userId;

	@TableField("picture")
	@ApiModelProperty("图片")
	private String picture;

	@TableField(exist = false)
	@ApiModelProperty("用户原始提示词,不包含其他指令")
	private String promptUserRaw;

	@TableField(exist = false)
	@ApiModelProperty("是否被收藏")
	private Boolean collectStatus;

	// Hidden -- start
	@TableField(exist = false)
	@JsonIgnore
	private String key;

	@TableField("final_prompt")
	//@JsonIgnore
	private String finalPrompt;

	@TableField("final_prompt")
	@JsonIgnore
	private String notifyHook;

	@TableField("related_task_id")
	//@JsonIgnore
	private String relatedTaskId;

	@TableField("message_id")
	//@JsonIgnore
	private String messageId;

	@TableField("message_hash")
	//@JsonIgnore
	private String messageHash;

	@TableField(exist = false)
	//@JsonIgnore
	private String userUploadImgUrl;

	@TableField(exist = false)
	//@JsonIgnore
	private String mjRawPrompt;

	@TableField(exist = false)
	@JsonIgnore
	private String reqDomain;

	@TableField(exist = false)
	private String lastChannelId;

	@TableField(exist = false)
	//图片宽度
	private Double imgWidth;

	@TableField(exist = false)
	//图片高度
	private Double imgHeight;


	// Hidden -- end

	@JsonIgnore
	private final transient Object lock = new Object();

	public void sleep() throws InterruptedException {
		synchronized (this.lock) {
			this.lock.wait();
		}
	}

	//唤醒在此对象的监视器上等待的所有线程
	public void awake() {
		synchronized (this.lock) {
			this.lock.notifyAll();
		}
	}

}
