package com.hanzaitu.ai.draw.domain.entity;


import com.hanzaitu.ai.draw.enums.Action;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import lombok.Data;

import java.math.BigInteger;

@Data
public class AiDrawRecordEntity {

    private BigInteger id;

    private String taskId;

    private String prompt;

    private String promptEn;

    private Long userId;

    private String submitTime;

    private String startTime;

    private String finishTime;

    private String mjRawDrawImage;

    private String mjNewDrawImage;

    private String userUploadImageUrl;

    private TaskStatus status;

    private String failReason;

    private Action type;

    private String description;

    private Integer progress = 0;

    private String messageId;

    private String taskKey;

    private String notifyHook;

    private String relatedTaskId;

    private String finalPrompt;

    private String messageHash;

    private String mjPrompt;

    private String channelId;

    private String promptUserRaw;

    private String picture;

    private Double imgWidth;

    private Double imgHeight;




}
