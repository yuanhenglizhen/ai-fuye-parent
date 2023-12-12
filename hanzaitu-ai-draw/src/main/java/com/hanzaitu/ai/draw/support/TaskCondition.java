package com.hanzaitu.ai.draw.support;

import cn.hutool.core.text.CharSequenceUtil;
import com.hanzaitu.ai.draw.enums.Action;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.function.Predicate;


@Data
@Accessors(chain = true)
public class TaskCondition implements Predicate<Task> {

	private String key;

	private String prompt;
	private String promptEn;
	private String finalPrompt;

	private String relatedTaskId;
	private String messageId;

	private String channelId;

	private Set<TaskStatus> statusSet;

	private Set<Action> actionSet;

	@Override
	public boolean test(Task task) {

		if (CharSequenceUtil.isNotBlank(this.channelId) && !this.channelId.equals(task.getChannelId())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.key) && !this.key.equals(task.getKey())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.prompt) && !this.prompt.equals(task.getPrompt())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.promptEn) && !this.promptEn.equals(task.getPromptEn())) {
			return false;
		}
		if (CharSequenceUtil.isNotBlank(this.finalPrompt) && !this.finalPrompt.equals(task.getFinalPrompt())) {
			return false;
		}

		if (CharSequenceUtil.isNotBlank(this.relatedTaskId) && !this.relatedTaskId.equals(task.getRelatedTaskId())) {
			return false;
		}

		if (CharSequenceUtil.isNotBlank(this.messageId) && !this.messageId.equals(task.getMessageId())) {
			return false;
		}

		if (this.statusSet != null && !this.statusSet.isEmpty() && !this.statusSet.contains(task.getStatus())) {
			return false;
		}

		if (this.actionSet != null && !this.actionSet.isEmpty() && !this.actionSet.contains(task.getAction())) {
			return false;
		}

		return true;
	}

}
