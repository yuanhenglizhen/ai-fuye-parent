package com.hanzaitu.ai.draw.support;

import com.hanzaitu.ai.draw.ProxyProperties;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import com.hanzaitu.ai.draw.service.TaskService;
import com.hanzaitu.common.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
	private final TaskService taskService;
	private final ProxyProperties properties;


	@Scheduled(fixedRate = 30000L)
	public void checkTasks() {
		long currentTime = System.currentTimeMillis();
		//long timeout = TimeUnit.MINUTES.toMillis(this.properties.getQueue().getTimeoutMinutes());
		Set<TaskStatus> statusSet = new HashSet<>();
		statusSet.add(TaskStatus.NOT_START);
		statusSet.add(TaskStatus.SUBMITTED);
		statusSet.add(TaskStatus.IN_PROGRESS);
		TaskCondition condition = new TaskCondition()
				.setStatusSet(statusSet);
		Stream<Task> taskStream = this.taskService.findTask(condition);
		if (taskStream == null) {
			log.error("定时任务查找运行任务失败！！");
			return;
		}
		taskStream.filter(t -> currentTime - DateUtils.parseDate(t.getStartTime()).getTime()
						> TimeUnit.MINUTES.toMillis(t.getTimeOut()))
				.forEach(task -> {
					task.setFinishTime(DateUtils.getTime());
					task.setFailReason("任务超时");
					task.setStatus(TaskStatus.FAILURE);
					task.awake();
					this.taskService.removeRunTask(task);
				});
	}
}
