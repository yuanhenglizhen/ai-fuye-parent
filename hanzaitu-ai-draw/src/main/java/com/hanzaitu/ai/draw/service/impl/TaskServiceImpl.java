package com.hanzaitu.ai.draw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.draw.ProxyProperties;
import com.hanzaitu.ai.draw.domain.Message;
import com.hanzaitu.ai.draw.domain.dto.ReleaseDrawDto;
import com.hanzaitu.ai.draw.domain.entity.AiDrawCollectEntity;
import com.hanzaitu.ai.draw.domain.entity.DiscordAuthTokenEntity;
import com.hanzaitu.ai.draw.enums.Action;
import com.hanzaitu.ai.draw.enums.TaskStatus;
import com.hanzaitu.ai.draw.enums.ZoomEnum;
import com.hanzaitu.ai.draw.handler.DrawKeyManager;
import com.hanzaitu.ai.draw.mapper.AiDrawCollectionMapper;
import com.hanzaitu.ai.draw.service.*;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.support.TaskCondition;
import com.hanzaitu.ai.draw.util.MimeTypeUtils;
import com.hanzaitu.ai.draw.util.RequestDomainContext;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.AiDrawQuadratCheckEntity;
import com.hanzaitu.common.enums.RecordTypeEnum;
import com.hanzaitu.common.enums.UserNotifyEnum;
import com.hanzaitu.common.enums.draw.CheckStatusEnum;
import com.hanzaitu.common.mapper.AiDrawQuadratCheckMapper;
import com.hanzaitu.common.params.WalletPointParam;
import com.hanzaitu.common.service.FinanceUserWalletService;
import com.hanzaitu.common.service.NotifyMsgCommonService;
import com.hanzaitu.common.utils.DateUtils;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.ServletUtils;
import com.hanzaitu.common.utils.StringUtils;
import eu.maxschuster.dataurl.DataUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import static com.hanzaitu.common.constant.CacheConstants.INTEGRAL_DEDUCTION;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

	@Resource
	private DbTaskStoreService taskStoreService;

	@Resource
	private DiscordService discordService;

	@Resource
	private NotifyService notifyService;

	@Resource
	private DrawKeyManager drawKeyManager;

	@Resource
	private AiDrawQuadratCheckMapper aiDrawQuadratCheckMapper;

	@Resource
	private AiDrawCollectionMapper aiDrawCollectionMapper;

	@Resource
	private NotifyMsgCommonService notifyMsgCommonService;

	@Resource
	private FinanceUserWalletService financeUserWalletService;


	private ConcurrentMap<String,String> taskDomainUrl = new ConcurrentHashMap<String,String>();

	@Resource
	private RedisCache redisCache;


	private ThreadPoolTaskExecutor taskExecutor;
	private List<Task> runningTasks;

	private ProxyProperties.TaskQueueConfig queueConfig;

	public TaskServiceImpl(ProxyProperties properties) {
		queueConfig = properties.getQueue();
	}

	@PostConstruct
	public void init() {
		//设置任务队列配置
		drawKeyManager.run();

		//初始化一个线程安全的list
		this.runningTasks = Collections.synchronizedList(new ArrayList<>(drawKeyManager.getCoreInt() * 2));
		if (drawKeyManager.getCoreInt() > 0) {
			this.taskExecutor = new ThreadPoolTaskExecutor();
			this.taskExecutor.setCorePoolSize(drawKeyManager.getCoreInt());
			this.taskExecutor.setMaxPoolSize(drawKeyManager.getCoreInt());
			this.taskExecutor.setQueueCapacity(queueConfig.getQueueSize());
			this.taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
			this.taskExecutor.setThreadNamePrefix("TaskQueue-");
			this.taskExecutor.initialize();
		}
	}

	@Override
	public Task getTask(String id) {
		Task task = this.runningTasks.stream().filter(t -> id.equals(t.getId())).findFirst().orElse(null);
		if (task == null) {
			task = this.runningTasks.stream().filter(t -> id.equals(t.getRelatedTaskId())).findFirst().orElse(null);
		}
		return task;
	}

	/**
	 * 找到符合条件的任务
	 * @param condition
	 * @return
	 */
	@Override
	public Stream<Task> findTask(TaskCondition condition) {
		return this.runningTasks.stream().filter(condition);
	}

	/**
	 * 提交生图任务
	 * @param task
	 * @return
	 */
	@Override
	public Message<String> submitImagine(Task task) {
		setDiscordInfo(task);
		log.debug("收到提交绘画任务：{}",task.getId());
		return submitTask(task, () -> {
			//组合请求参数
			Message<Void> result = this.discordService.imagine(task);

			checkAndWait(task, result);
		});
	}

	/**
	 * 提交优化提示词任务
	 * @param task
	 * @return
	 */
	@Override
	public Message<String> submitShorten(Task task) {
		setDiscordInfo(task);
		log.debug("收到提交优化提示词任务：{}",task.getId());
		return submitTask(task, () -> {
			//组合请求参数
			Message<Void> result = this.discordService.shorten(task);

			checkAndWait(task, result);
		});
	}

	/**
	 * 提交放大指令任务
	 * @param task
	 * @param targetMessageId
	 * @param targetMessageHash
	 * @param index
	 * @return
	 */
	@Override
	public Message<String> submitUpscale(Task task, String targetMessageId, String targetMessageHash, int index) {
		setDiscordInfo(task);
		return submitTask(task, () -> {
			Message<Void> result = this.discordService.upscale(task,targetMessageId, index, targetMessageHash);
			checkAndWait(task, result);
		});
	}

	/**
	 * 提交V指令任务
	 * @param task
	 * @param targetMessageId
	 * @param targetMessageHash
	 * @param index
	 * @return
	 */
	@Override
	public Message<String> submitVariation(Task task, String targetMessageId, String targetMessageHash, int index) {
		setDiscordInfo(task);
		return submitTask(task, () -> {
			Message<Void> result = this.discordService.variation(task,targetMessageId, index, targetMessageHash);
			checkAndWait(task, result);
		});
	}

	/**
	 * 提交图生文任务
	 * @param task
	 * @param dataUrl
	 * @return
	 */
	@Override
	public Message<String> submitDescribe(Task task, DataUrl dataUrl) {
		setDiscordInfo(task);
		return submitTask(task, () -> {
			String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
			Message<String> uploadResult = this.discordService.upload(task,taskFileName, dataUrl);

			if (uploadResult.getCode() != Message.SUCCESS_CODE) {
				task.setFinishTime(DateUtils.getTime());
				task.setFailReason(uploadResult.getDescription());
				changeStatusAndNotify(task, TaskStatus.FAILURE);
				return;
			}
			String finalFileName = uploadResult.getResult();
			Message<Void> result = this.discordService.describe(task,finalFileName);
			checkAndWait(task, result);
		});
	}

	/**
	 * 提交风格相似指令任务(激进风格)
	 * @param task
	 * @param targetMessageId
	 * @param targetMessageHash
	 * @return
	 */
	@Override
	public Message<String> submitHighVariation(Task task, String targetMessageId, String targetMessageHash) {
		setDiscordInfo(task);
		return submitTask(task, () -> {
			Message<Void> result = this.discordService.highVariation(task,targetMessageId,targetMessageHash);
			checkAndWait(task, result);
		});
	}

	/**
	 * 提交风格相似指令任务(保守风格)
	 * @param task
	 * @param targetMessageId
	 * @param targetMessageHash
	 * @return
	 */
	@Override
	public Message<String> submitLowVariation(Task task, String targetMessageId, String targetMessageHash) {
		setDiscordInfo(task);
		return submitTask(task, () -> {
			Message<Void> result = this.discordService.lowVariation(task,targetMessageId,targetMessageHash);
			checkAndWait(task, result);
		});
	}


	/**
	 * 镜头拉近
	 * @param task
	 * @param messageHash
	 * @param zoom
	 * @return
	 */
	@Override
	public Message<String> submitZoom(Task task, String messageId, String messageHash, ZoomEnum zoomEnum) {
		setDiscordInfo(task);
		return submitTask(task, () -> {
			Message<Void> result = this.discordService.zoom(task,messageId, messageHash, zoomEnum);
			checkAndWait(task, result);
		});
	}


	private Message<String> submitTask(Task task, Runnable runnable) {
		this.taskStoreService.saveTask(task);
		int size;
		try {
			setDomain(task.getId());
			size = this.taskExecutor.getThreadPoolExecutor().getQueue().size();
			this.taskExecutor.execute(() -> {
				task.setStartTime(DateUtils.getTime());
				log.debug("添加runningTask:{}",task.getId());
				this.runningTasks.add(task);
				try {
					this.taskStoreService.saveTask(task);
					//发起请求
					runnable.run();
				} catch (Exception e){
					task.setFinishTime(DateUtils.getTime());
					task.setFailReason("接口请求失败");
					task.setStatus(TaskStatus.FAILURE);
					changeStatusAndNotify(task,TaskStatus.FAILURE);
					throw e;
				}finally {
					log.debug("删除业务id:{}",task.getId());
					this.runningTasks.remove(task);
					rebatePoints(task);
				}
			});
		} catch (RejectedExecutionException e) {
			this.taskStoreService.deleteTask(task.getId());
			rebatePoints(task);
			return Message.failure("队列已满，请稍后尝试");
		}
		if (size == 0) {
			return Message.success(task.getId());
		} else {
			return Message.success(Message.WAITING_CODE, "排队中，前面还有" + size + "个任务", task.getId());
		}
	}

	private void checkAndWait(Task task, Message<Void> result) {
		if (result.getCode() != Message.SUCCESS_CODE) {
			task.setFinishTime(DateUtils.getTime());
			task.setFailReason(result.getDescription());
			changeStatusAndNotify(task, TaskStatus.FAILURE);
			return;
		}
		changeStatusAndNotify(task, TaskStatus.SUBMITTED);
		do {
			log.debug(JacksonUtil.toJsonString(task));
			try {
				task.sleep();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			log.debug("检查任务状态1");
			changeStatusAndNotify(task, task.getStatus());
			//如果执行中 就不停的去更新任务状态
		} while (task.getStatus() == TaskStatus.IN_PROGRESS);
		log.debug("task finished, id: {}, status: {}", task.getId(), task.getStatus());
	}

	private void changeStatusAndNotify(Task task, TaskStatus status) {
		log.debug("更新任务状态");
		log.debug(JacksonUtil.toJsonString(task));
		task.setStatus(status);
		this.taskStoreService.saveTask(task);
		this.notifyService.notifyTaskChange(task);
		rebatePoints(task);
	}

	public void removeRunTask(Task task) {
		this.runningTasks.remove(task);
	}

	private void setDiscordInfo(Task task) {
		log.debug("获取到Action：{},获取到key：{},判断是否等于 UPSCALE ：{}, 判断是否等于VARIATION：{}, LastChannelId ：{}",task.getAction(),task.getKey()
				,task.getAction().equals(Action.UPSCALE),task.getAction().equals(Action.VARIATION),task.getLastChannelId());
		DiscordAuthTokenEntity discordAuthTokenEntity = null;
		if ((task.getAction().equals(Action.UPSCALE) || task.getAction().equals(Action.VARIATION)
				|| task.getKey().endsWith(Action.UPSCALE.toString()) || task.getKey().endsWith(Action.VARIATION.toString()))
				&& StringUtils.isNotEmpty(task.getLastChannelId())) {

			List<DiscordAuthTokenEntity> discordAuthTokenEntityList = drawKeyManager.getDiscordAuthTokenEntityList();
			discordAuthTokenEntity = discordAuthTokenEntityList.stream()
					.filter(k->k.getChannelId().equals(task.getLastChannelId())).findFirst().orElse(null);
		}else {
			//轮询discord的Key
			discordAuthTokenEntity = drawKeyManager.getQueueKey();
			log.debug("获取到key-id:{}", discordAuthTokenEntity.getTokenId());
			DiscordAuthTokenEntity finalDiscordAuthTokenEntity = discordAuthTokenEntity;
			long findCount = this.runningTasks.stream().filter(r -> Objects.equals(r.getTokenId(), finalDiscordAuthTokenEntity.getTokenId())).count();
			log.debug("获取到key-id:{} ,任务运行数据量:{} , 当前线程池运行数量: {}, 当前线程池正在运行的线程池数量: {}, 当前线程池队列长度: {}"
					, discordAuthTokenEntity.getTokenId(), findCount,
					this.taskExecutor.getThreadPoolExecutor().getPoolSize(), this.taskExecutor.getThreadPoolExecutor().getActiveCount(),
					this.taskExecutor.getThreadPoolExecutor().getQueue().size());
			//如果当前运行数量小于当前Key核心数那就
			if (this.runningTasks.size() > finalDiscordAuthTokenEntity.getCoreSize()
					&& findCount >= finalDiscordAuthTokenEntity.getCoreSize()) {
				//轮询下一个key
				discordAuthTokenEntity = drawKeyManager.getQueueKey();
			}
		}
		setTaskInfo(task,discordAuthTokenEntity);
	}

	private void setTaskInfo(Task task, DiscordAuthTokenEntity discordAuthTokenEntity) {
		task.setGuildId(discordAuthTokenEntity.getGuildId());
		task.setTokenId(discordAuthTokenEntity.getTokenId());
		task.setChannelId(discordAuthTokenEntity.getChannelId());
		task.setBotToken(discordAuthTokenEntity.getBotToken());
		task.setUserToken(discordAuthTokenEntity.getUserToken());
		task.setCoreSize(discordAuthTokenEntity.getCoreSize());
		task.setProxyHost(discordAuthTokenEntity.getProxyHost());
		task.setProxyPort(discordAuthTokenEntity.getProxyPort());
		task.setTimeOut(discordAuthTokenEntity.getTimeOut());
	}

	/**
	 * 回退积分
	 * @param task
	 */
	private void rebatePoints(Task task) {
		String cacheKey = INTEGRAL_DEDUCTION+":"+task.getId();
		WalletPointParam walletPointParam = redisCache.getCacheObject(cacheKey);
		log.debug("任务状态：{}, 回退参数:{}",task.getStatus(),JacksonUtil.toJsonString(walletPointParam));
		if (task.getStatus().equals(TaskStatus.FAILURE) && walletPointParam != null) {
			delDomain(task.getId());
			financeUserWalletService.returnGptPoints(RecordTypeEnum.MIDJOURNEY,
					Math.abs(Math.abs(walletPointParam.getPoints())),walletPointParam.getUserId());
			notifyMsgCommonService.publishUserMessage(task.getUserId(),
					String.format(UserNotifyEnum.REBATE_POINTS.getMsg(), RecordTypeEnum.MIDJOURNEY.getDescription()));
			redisCache.deleteObject(cacheKey);
		} else if (task.getStatus().equals(TaskStatus.SUCCESS)) {
			delDomain(task.getId());
			redisCache.deleteObject(cacheKey);
		}
	}

	/**
	 * 发布绘画
	 * @return
	 */
	@Override
	public AjaxResult releaseDraw(ReleaseDrawDto releaseDrawDto) {
		Long userId = UserThreadLocal.get().getUserId();
		Task task = taskStoreService.getTask(releaseDrawDto.getTaskId());

		if (task == null) {
			return AjaxResult.error("当前绘画任务不存在");
		}

		if (!task.getStatus().equals(TaskStatus.SUCCESS) || (!task.getAction().equals(Action.UPSCALE))) {
			return AjaxResult.error("当前绘画任务不支持发布绘画广场");
		}

		Long aiDrawQuadratCheckCount = aiDrawQuadratCheckMapper.selectCount(
				new QueryWrapper<AiDrawQuadratCheckEntity>().eq("task_id",releaseDrawDto.getTaskId()));
		if (aiDrawQuadratCheckCount > 0) {
			return AjaxResult.error("当前绘画任务已经被发布过");
		}

		AiDrawQuadratCheckEntity aiDrawQuadratCheckEntity = new AiDrawQuadratCheckEntity();
		aiDrawQuadratCheckEntity.setUserId(userId.intValue());
		aiDrawQuadratCheckEntity.setCheckStatus(CheckStatusEnum.WAIT_CHECK.getCode());
		aiDrawQuadratCheckEntity.setTaskId(task.getId());
		return aiDrawQuadratCheckMapper.insert(aiDrawQuadratCheckEntity) > 0 ? AjaxResult.success() : AjaxResult.error();
	}

	@Override
	public AjaxResult collect(ReleaseDrawDto releaseDrawDto) {
		Long userId = UserThreadLocal.get().getUserId();
		Task task = taskStoreService.getTask(releaseDrawDto.getTaskId());
		if (task == null) {
			return AjaxResult.error("当前绘画任务不存在");
		}
		if (!task.getStatus().equals(TaskStatus.SUCCESS)
				|| (!task.getAction().equals(Action.UPSCALE) && !task.getAction().equals(Action.LOW_ZOOM)
				&& !task.getAction().equals(Action.HIGH_ZOOM)
				&& !task.getAction().equals(Action.HIGH_VARIATION)
				&& !task.getAction().equals(Action.LOW_VARIATION))) {
			return AjaxResult.error("当前绘画任务不支持收藏");
		}
		Long dataCount = aiDrawCollectionMapper.selectCount(
				new QueryWrapper<AiDrawCollectEntity>().eq("task_id", releaseDrawDto.getTaskId()));
		if (dataCount == 0) {
			AiDrawCollectEntity aiDrawCollect = new AiDrawCollectEntity();
			aiDrawCollect.setPrompt(task.getPromptEn());
			aiDrawCollect.setImgUrl(task.getPicture());
			aiDrawCollect.setTaskId(task.getId());
			aiDrawCollect.setUserId(userId);
			aiDrawCollect.setImgHeight(task.getImgHeight());
			aiDrawCollect.setImgWidth(task.getImgWidth());
			aiDrawCollect.setUserName(UserThreadLocal.get().getNickName());
			return aiDrawCollectionMapper.insert(aiDrawCollect) > 0 ? AjaxResult.success("收藏成功").put(
					"collectStatus",true) : AjaxResult.error().put("collectStatus",false);
		} else {
			return aiDrawCollectionMapper.delete(new QueryWrapper<AiDrawCollectEntity>().eq(
					"task_id",releaseDrawDto.getTaskId())) > 0 ? AjaxResult.success("取消收藏成功").put("collectStatus",false) : AjaxResult.error().put("collectStatus",false);
		}

	}


	private void setDomain(String taskId) {
		HttpServletRequest request =  ServletUtils.getRequest();
		String originHost = request.getHeader("Hzt-Origin");
		String oriHost = request.getHeader("Host");
		String localDomain = StringUtils.isEmpty(originHost) ? "http://"+oriHost : originHost;
		taskDomainUrl.put(taskId,localDomain);
	}

	private void delDomain(String taskId) {
		taskDomainUrl.remove(taskId);
	}


	public ConcurrentMap<String,String> getTaskDomain() {
		return taskDomainUrl;
	}
}