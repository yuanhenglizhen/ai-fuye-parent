package com.hanzaitu.ai.draw.service;


import com.hanzaitu.ai.draw.support.Task;

import java.util.List;

public interface TaskStoreService {

	void saveTask(Task task);

	void deleteTask(String id);

	Task getTask(String id);

	List<Task> listTask();

}
