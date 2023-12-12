package com.hanzaitu.ai.draw.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import com.hanzaitu.ai.draw.ProxyProperties;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.domain.Message;
import com.hanzaitu.ai.draw.domain.dto.*;
import com.hanzaitu.ai.draw.enums.Action;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import com.hanzaitu.ai.draw.enums.ZoomEnum;
import com.hanzaitu.ai.draw.service.*;
import com.hanzaitu.ai.draw.service.impl.QiniuService;
import com.hanzaitu.ai.draw.support.BannedPromptHelper;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.util.ConvertUtils;
import com.hanzaitu.ai.draw.util.FileUtil;
import com.hanzaitu.ai.draw.util.MimeTypeUtils;
import com.hanzaitu.ai.draw.util.UVData;
import com.hanzaitu.common.annotation.ReqLimit;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.FinanceUserWallet;
import com.hanzaitu.common.domain.server.Sys;
import com.hanzaitu.common.enums.RecordTypeEnum;
import com.hanzaitu.common.params.WalletPointParam;
import com.hanzaitu.common.service.FinanceUserWalletService;
import com.hanzaitu.common.utils.DateUtils;
import com.hanzaitu.common.utils.RegexUtils;
import com.hanzaitu.common.utils.uuid.UUID;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hanzaitu.common.constant.CacheConstants.INTEGRAL_DEDUCTION;

@Api(tags = "任务提交")
@RestController
@RequestMapping("/draw/trigger")
@RequiredArgsConstructor
public class TriggerController {

	private final TranslateService translateService;

	@Resource
	private RedisCache redisCache;

	@Autowired
	private final DbTaskStoreService taskStoreService;

	@Autowired
	private UploadLoadConfig uploadLoadConfig;

	private final ProxyProperties properties;

	private final BannedPromptHelper bannedPromptHelper;

	private final TaskService taskService;

	@Autowired
	private FinanceUserWalletService financeUserWalletService;

	@ApiOperation(value = "提交Imagine或UV任务")
	@PostMapping("/submit")
	@ReqLimit(permitsPerSecond = 20)
	public Message<String> submit(@Validated @RequestBody SubmitDTO submitDTO) {

		if (submitDTO.getAction() == null) {
			return Message.validationError();
		}

		if ((submitDTO.getAction() == Action.UPSCALE || submitDTO.getAction() == Action.VARIATION)
				&& (submitDTO.getIndex() < 1 || submitDTO.getIndex() > 4)) {
			return Message.validationError();
		}

		Task task = new Task();

		task.setUserId(UserThreadLocal.get().getUserId());

		task.setReqDomain(UserThreadLocal.get().getOperationDomain());

		//设置任务变更的回调地址
		task.setNotifyHook(CharSequenceUtil.isBlank(submitDTO.getNotifyHook()) ? this.properties.getNotifyHook()
				: submitDTO.getNotifyHook());

		String ids = RandomUtil.randomNumbers(16);

		WalletPointParam financeUserWallet = financeUserWalletService.expendGptPoints(RecordTypeEnum.MIDJOURNEY);
		//设置预扣金额
		redisCache.setCacheObject(INTEGRAL_DEDUCTION+":"+ids,financeUserWallet,10, TimeUnit.MINUTES);

		//任务ID
		task.setId(ids);

		//任务创建时间
		task.setSubmitTime(DateUtils.getTime());

		//自定义字段
		task.setState(submitDTO.getState());

		//动作: IMAGINE\UPSCALE\VARIATION\RESET.
		task.setAction(submitDTO.getAction());
		task.setCdnHost(uploadLoadConfig.getQnyImageDomain());

		if (Action.IMAGINE.equals(submitDTO.getAction())) {


			String prompt = submitDTO.getPrompt();
			if (CharSequenceUtil.isBlank(prompt)) {
				return Message.validationError();
			}
			task.setKey(task.getId());
			task.setPrompt(prompt);
			String promptEn;
			int paramStart = prompt.indexOf(" --");
			List<String> matchesStr = RegexUtils.getMatches(".(png|jpg|jpeg|webp)\\s",prompt);
			int spaceStr = 0;
			String imgPath = "";
			if (matchesStr.size() > 0) {
				String findMatcheStr = matchesStr.get(matchesStr.size() -1);
				spaceStr = prompt.lastIndexOf(findMatcheStr)+findMatcheStr.length();
				imgPath = prompt.substring(0,spaceStr);
			}
			if (paramStart > 0) {
				promptEn = this.translateService.translateToEnglish(prompt.substring(spaceStr, paramStart).trim()).trim() + prompt.substring(paramStart);
			} else if (spaceStr > 0){
				promptEn = this.translateService.translateToEnglish(prompt.substring(spaceStr).trim()).trim();
			} else {
				promptEn = this.translateService.translateToEnglish(prompt).trim();
			}
			if (this.bannedPromptHelper.isBanned(promptEn)) {
				return Message.of(Message.VALIDATION_ERROR_CODE, "可能包含敏感词");
			}
			// 图片和文字拼接
			promptEn = imgPath +promptEn;
			task.setPromptEn(promptEn);
			task.setFinalPrompt("[" + task.getId() + "] " + promptEn);
			task.setDescription("/imagine " + submitDTO.getPrompt());
			task.setPromptUserRaw(submitDTO.getPromptUserRaw());
			return this.taskService.submitImagine(task);
		}
		if (CharSequenceUtil.isBlank(submitDTO.getTaskId())) {
			return Message.validationError();
		}

		Task targetTask = this.taskStoreService.getTask(submitDTO.getTaskId());
		if (targetTask == null) {
			return Message.of(Message.VALIDATION_ERROR_CODE, "任务不存在或已失效");
		}
		if (!TaskStatus.SUCCESS.equals(targetTask.getStatus())) {
			return Message.of(Message.VALIDATION_ERROR_CODE, "关联任务状态错误");
		}
		task.setPrompt(targetTask.getPrompt());
		task.setPromptEn(targetTask.getPromptEn());
		task.setFinalPrompt(targetTask.getFinalPrompt());
		task.setRelatedTaskId(ConvertUtils.findTaskIdByFinalPrompt(targetTask.getFinalPrompt()));
		task.setKey(targetTask.getMessageId() + "-" + submitDTO.getAction());
		task.setLastChannelId(targetTask.getLastChannelId());
		task.setPromptUserRaw(targetTask.getPromptUserRaw());
		if (Action.UPSCALE.equals(submitDTO.getAction())) {
			task.setDescription("/up " + submitDTO.getTaskId() + " U" + submitDTO.getIndex());
			return this.taskService.submitUpscale(task, targetTask.getMessageId(), targetTask.getMessageHash(),
					submitDTO.getIndex());
		} else if (Action.VARIATION.equals(submitDTO.getAction())) {
			task.setDescription("/up " + submitDTO.getTaskId() + " V" + submitDTO.getIndex());
			return this.taskService.submitVariation(task, targetTask.getMessageId(), targetTask.getMessageHash(), submitDTO.getIndex());
		} else if (Action.HIGH_VARIATION.equals(submitDTO.getAction())) {

			task.setDescription("/variation " + submitDTO.getTaskId());
			task.setKey(targetTask.getMessageId() + "-" +Action.VARIATION);
			return this.taskService.submitHighVariation(task, targetTask.getMessageId(), targetTask.getMessageHash());
		} else if (Action.LOW_VARIATION.equals(submitDTO.getAction())) {

			task.setDescription("/variation " + submitDTO.getTaskId());
			task.setKey(targetTask.getMessageId() + "-" +Action.VARIATION);
			return this.taskService.submitLowVariation(task, targetTask.getMessageId(), targetTask.getMessageHash());
		}else if(Action.HIGH_ZOOM.equals(submitDTO.getAction())){
			task.setKey(targetTask.getMessageId() + "-" + Action.UPSCALE);
			return this.taskService.submitZoom(task,targetTask.getMessageId(), targetTask.getMessageHash(),
					ZoomEnum.ZOOM_HIGH);
		} else if (Action.LOW_ZOOM.equals(submitDTO.getAction())){
			task.setKey(targetTask.getMessageId() + "-" + Action.UPSCALE);
			return this.taskService.submitZoom(task,targetTask.getMessageId(), targetTask.getMessageHash(),
					ZoomEnum.ZOOM_LOW);
		}else {
			return Message.of(Message.VALIDATION_ERROR_CODE, "不支持的操作");
		}
	}

	@ApiOperation(value = "删除任务")
	@PostMapping("/del")
	public Message deleteTask(@RequestBody DeleteTaskDto deleteTaskDto) throws MalformedURLException {
		return this.taskStoreService.deleteTask(deleteTaskDto.getTaskId());
	}

	@ApiOperation(value = "提交Describe图生文任务")
	@PostMapping("/describe")
	public Message<String> describe(@RequestBody DescribeDTO describeDTO) throws MalformedURLException {
		if (CharSequenceUtil.isBlank(describeDTO.getImgUrl())) {
			return Message.validationError();
		}
		String base64Str =  taskStoreService.imageConvertBase64(describeDTO.getImgUrl());
		IDataUrlSerializer serializer = new DataUrlSerializer();
		DataUrl dataUrl;
		try {
			dataUrl = serializer.unserialize(base64Str);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Message.of(Message.VALIDATION_ERROR_CODE, "base64格式错误");
		}
		Task task = new Task();
		task.setUserId(UserThreadLocal.get().getUserId());
		task.setSubmitTime(DateUtils.getTime());
		task.setId(RandomUtil.randomNumbers(16));
		String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
		task.setState(describeDTO.getState());
		//task.setNewImageUrl(FileUtil.getFileNameByUrl(describeDTO.getImgUrl()));
		task.setPicture(describeDTO.getImgUrl());
		task.setAction(Action.DESCRIBE);
		task.setDescription("/describe " + taskFileName);
		task.setKey(taskFileName);
		task.setNotifyHook(CharSequenceUtil.isBlank(describeDTO.getNotifyHook()) ? this.properties.getNotifyHook() : describeDTO.getNotifyHook());
		return this.taskService.submitDescribe(task, dataUrl);
	}

	@ApiOperation(value = "上传图片文件")
	@PostMapping("/upload-img")
	public Message<String> uploadImg(UploadImageDto uploadImageDto) {
		return taskStoreService.imageUploadWeb(uploadImageDto);
	}

	@ApiOperation(value = "发布绘画广场")
	@PostMapping("/release")
	public AjaxResult releaseDraw(@Validated @RequestBody ReleaseDrawDto uploadImageDto) {
		return taskService.releaseDraw(uploadImageDto);
	}
	@ApiOperation(value = "收藏绘画")
	@PostMapping("/collect")
	@ReqLimit(permitsPerSecond = 20)
	public AjaxResult collectDraw(@Validated @RequestBody ReleaseDrawDto uploadImageDto) {
		return taskService.collect(uploadImageDto);
	}

	@PostMapping("/test")
	public void testDb() {
		taskStoreService.updatePicture();
	}

}
