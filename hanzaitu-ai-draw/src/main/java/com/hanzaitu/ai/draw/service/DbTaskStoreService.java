package com.hanzaitu.ai.draw.service;

import com.hanzaitu.ai.draw.domain.Message;
import com.hanzaitu.ai.draw.domain.dto.UploadImageDto;
import com.hanzaitu.ai.draw.domain.vo.AiDrawPromptVo;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.PageParam;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface DbTaskStoreService {

    void saveTask(Task task);

    Message deleteTask(String id);

    Task getTask(String id);

    List<Task> getTaskList();

    HztPage<Task> getTaskList(PageParam pageParam);

    Task getTaskByTaskIdNoUser(String id);

    String imageConvertBase64(String imgPath);

    Message<String> imageUploadWeb(UploadImageDto uploadImageDto);

    List<AiDrawPromptVo> selectPromptList();

    void  updatePicture();
}
