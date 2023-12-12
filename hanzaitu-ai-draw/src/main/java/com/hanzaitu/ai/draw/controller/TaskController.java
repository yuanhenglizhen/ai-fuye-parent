package com.hanzaitu.ai.draw.controller;

import com.hanzaitu.ai.draw.service.DbTaskStoreService;
import com.hanzaitu.ai.draw.service.TaskStoreService;
import com.hanzaitu.ai.draw.service.impl.QiniuService;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.util.FileUtil;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.page.PageParam;
import com.hanzaitu.common.utils.file.FileUploadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Api(tags = "任务查询")
@RestController
@RequestMapping("/draw/task")
@RequiredArgsConstructor
public class TaskController {

	private final DbTaskStoreService dbTaskStoreService;

	private final QiniuService qiniuService;

	@ApiOperation(value = "查询所有任务")
	@PostMapping("/list")
	public AjaxResult listTask(@RequestBody PageParam pageParam) {
		return AjaxResult.success(this.dbTaskStoreService.getTaskList(pageParam));
	}

	@ApiOperation(value = "指定ID获取任务")
	@GetMapping("/{id}/fetch")
	public AjaxResult getTask(@ApiParam(value = "任务ID") @PathVariable String id) {
		return AjaxResult.success(this.dbTaskStoreService.getTask(id));
	}

	@ApiOperation(value = "查询提示词")
	@GetMapping("/promp")
	public AjaxResult getPrompt() {
		return AjaxResult.success(this.dbTaskStoreService.selectPromptList());
	}

}
