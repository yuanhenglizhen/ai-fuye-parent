package com.hanzaitu.ai.chat.domain;

import com.plexpt.chatgpt.entity.chat.Message;
import lombok.Data;

@Data
public class ChatRecord  {

    private String winId;

    private Integer userId;

    private Integer sort;

    private Message content;

    private String time;

    private String userName;

}
