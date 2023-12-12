package com.hanzaitu.ai.draw.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.ListUtil;
import com.hanzaitu.ai.draw.service.TaskStoreService;
import com.hanzaitu.ai.draw.support.Task;

import java.time.Duration;
import java.util.List;

public class InMemoryTaskStoreServiceImpl implements TaskStoreService {
	private final TimedCache<String, Task> taskMap;

	public InMemoryTaskStoreServiceImpl(Duration timeout) {
		this.taskMap = CacheUtil.newTimedCache(timeout.toMillis());
	}

	@Override
	public void saveTask(Task task) {
		this.taskMap.put(task.getId(), task);
	}

	@Override
	public void deleteTask(String key) {
		this.taskMap.remove(key);
	}

	@Override
	public Task getTask(String key) {
		return this.taskMap.get(key);
	}

	@Override
	public List<Task> listTask() {
		return ListUtil.toList(this.taskMap.iterator());
	}

}
