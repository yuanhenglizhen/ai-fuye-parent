package com.hanzaitu.ai.draw.util;

import com.hanzaitu.ai.draw.enums.Action;
import lombok.Data;

@Data
public class MessageData {
	private Action action;
	private String prompt;
	private int index;
	private String status;
	//private Integer progress;
}
