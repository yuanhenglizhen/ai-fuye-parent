package com.hanzaitu.ai.draw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.domain.Message;
import com.hanzaitu.ai.draw.domain.dto.UploadImageDto;
import com.hanzaitu.ai.draw.domain.entity.AiDrawPromptEntity;
import com.hanzaitu.ai.draw.domain.entity.AiDrawRecordEntity;
import com.hanzaitu.ai.draw.domain.entity.DiscordAuthTokenEntity;
import com.hanzaitu.ai.draw.domain.vo.AiDrawPromptCategoryVo;
import com.hanzaitu.ai.draw.domain.vo.AiDrawPromptContentVo;
import com.hanzaitu.ai.draw.domain.vo.AiDrawPromptVo;
import com.hanzaitu.ai.draw.handler.DrawKeyManager;
import com.hanzaitu.ai.draw.mapper.AiDrawPromptMapper;
import com.hanzaitu.ai.draw.mapper.AiDrawRecordMapper;
import com.hanzaitu.ai.draw.service.DbTaskStoreService;
import com.hanzaitu.ai.draw.service.DrawSquareService;
import com.hanzaitu.ai.draw.service.TaskService;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.util.FileUtil;
import com.hanzaitu.common.config.ServerConfig;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.PageParam;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.ServletUtils;
import com.hanzaitu.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class DbTaskStoreServiceImpl implements DbTaskStoreService {

    @Autowired
    private UploadLoadConfig uploadLoadConfig;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private AiDrawRecordMapper aiDrawRecordMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AiDrawPromptMapper aiDrawPromptMapper;

    @Autowired
    private DrawSquareService drawSquareService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private TaskService taskService;

    @Resource
    private DrawKeyManager drawKeyManager;

    @Override
    public void saveTask(Task task) {
        AiDrawRecordEntity aiDrawRecordEntity = new AiDrawRecordEntity();
        aiDrawRecordEntity.setTaskId(task.getId());
        aiDrawRecordEntity.setPrompt(task.getPrompt());
        aiDrawRecordEntity.setPromptEn(task.getPromptEn());
        aiDrawRecordEntity.setMjRawDrawImage(task.getImageUrl());
        aiDrawRecordEntity.setFailReason(task.getFailReason());
        aiDrawRecordEntity.setSubmitTime(task.getSubmitTime());
        aiDrawRecordEntity.setStartTime(task.getStartTime());
        aiDrawRecordEntity.setFinishTime(task.getFinishTime());
        aiDrawRecordEntity.setFailReason(task.getFailReason());
        aiDrawRecordEntity.setStatus(task.getStatus());
        aiDrawRecordEntity.setUserId(task.getUserId());
        aiDrawRecordEntity.setProgress(task.getProgress());
        aiDrawRecordEntity.setType(task.getAction());
        aiDrawRecordEntity.setRelatedTaskId(task.getRelatedTaskId());
        aiDrawRecordEntity.setMessageHash(task.getMessageHash());
        aiDrawRecordEntity.setNotifyHook(task.getNotifyHook());
        aiDrawRecordEntity.setFinalPrompt(task.getFinalPrompt());
        aiDrawRecordEntity.setTaskKey(task.getKey());
        aiDrawRecordEntity.setMessageId(task.getMessageId());
        aiDrawRecordEntity.setUserUploadImageUrl(task.getUserUploadImgUrl());
        aiDrawRecordEntity.setMjNewDrawImage(task.getNewImageUrl());
        aiDrawRecordEntity.setMjPrompt(task.getMjRawPrompt());
        aiDrawRecordEntity.setChannelId(task.getChannelId());
        aiDrawRecordEntity.setPicture(task.getPicture());
        aiDrawRecordEntity.setPromptUserRaw(task.getPromptUserRaw());
        aiDrawRecordEntity.setImgWidth(task.getImgWidth());
        aiDrawRecordEntity.setImgHeight(task.getImgHeight());
        if(aiDrawRecordMapper.findDrawRecordEntityByTaskId(task.getId(),task.getUserId()) == null) {
            aiDrawRecordMapper.insertDrawRecordEntity(aiDrawRecordEntity);
        }else {
            aiDrawRecordMapper.updateDrawRecordEntity(aiDrawRecordEntity);
        }
        task.setCdnHost(uploadLoadConfig.getQnyImageDomain());
        task.setLastChannelId(task.getChannelId());
        redisCache.setCacheMapValue(CacheConstants.DRAW_TASK_CACHE+task.getUserId(), task.getId(), task);
        log.debug("更新任务：{}",JacksonUtil.toJsonString(task));
    }

    public List<Task> getTaskList() {
        Long userId = UserThreadLocal.get().getUserId();
        List<Task> taskList = new ArrayList<>();
        //查询缓存
        List<T> recordCache = redisCache.getAllCacheMapValue(CacheConstants.DRAW_TASK_CACHE + userId);
        if (recordCache != null && !recordCache.isEmpty()) {
            for (Object v :recordCache) {
                Task val = (Task) v;
                Task findTask = taskService.getTask(((Task) v).getId());
                if (findTask != null) {
                    ((Task) v).setProgress(findTask.getProgress());
                }
                val.setPicture(putImgUrl(val.getPicture()));
                taskList.add(val);
            }
            //排序
            taskList.sort(Comparator.comparing(Task::getSubmitTime).reversed());
        }else {
            List<AiDrawRecordEntity> resAiDrawRecordEntityList = aiDrawRecordMapper.selectDrawRecordEntityByUserId(userId);
            if (resAiDrawRecordEntityList != null) {
                for (AiDrawRecordEntity aiDrawRecordEntityVal : resAiDrawRecordEntityList) {
                    try {
                        Task task = new Task();
                        task.setFinishTime(aiDrawRecordEntityVal.getFinishTime());
                        task.setAction(aiDrawRecordEntityVal.getType());
                        task.setId(aiDrawRecordEntityVal.getTaskId());
                        task.setSubmitTime(aiDrawRecordEntityVal.getSubmitTime());
                        task.setStartTime(aiDrawRecordEntityVal.getStartTime());
                        task.setStatus(aiDrawRecordEntityVal.getStatus());
                        task.setDescription(aiDrawRecordEntityVal.getDescription());
                        task.setFailReason(aiDrawRecordEntityVal.getFailReason());
                        task.setPrompt(aiDrawRecordEntityVal.getPrompt());
                        task.setPromptEn(aiDrawRecordEntityVal.getPromptEn());
                        task.setProgress(aiDrawRecordEntityVal.getProgress());
                        Task findTask = taskService.getTask(aiDrawRecordEntityVal.getTaskId());
                        if (findTask != null) {
                            task.setProgress(findTask.getProgress());
                        }
                        task.setUserId(aiDrawRecordEntityVal.getUserId());
                        task.setRelatedTaskId(aiDrawRecordEntityVal.getRelatedTaskId());
                        task.setMessageHash(aiDrawRecordEntityVal.getMessageHash());
                        task.setNotifyHook(aiDrawRecordEntityVal.getNotifyHook());
                        task.setFinalPrompt(aiDrawRecordEntityVal.getFinalPrompt());
                        task.setKey(aiDrawRecordEntityVal.getTaskKey());
                        task.setMessageId(aiDrawRecordEntityVal.getMessageId());
                        task.setNewImageUrl(aiDrawRecordEntityVal.getMjNewDrawImage());
                        task.setLastChannelId(aiDrawRecordEntityVal.getChannelId());
                        task.setPicture(putImgUrl(aiDrawRecordEntityVal.getPicture()));
                        redisCache.setCacheMapValue(CacheConstants.DRAW_TASK_CACHE+userId, task.getId(),task);
                        taskList.add(task);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return taskList;
    }


    public HztPage<Task> getTaskList(PageParam pageParam) {
        Long userId = UserThreadLocal.get().getUserId();
        HztPage<Task> resAiDrawRecordEntityList = aiDrawRecordMapper.selectPage(pageParam,
                new QueryWrapper<Task>().orderBy(true,false,"submit_time").eq("user_id",userId));

        if (resAiDrawRecordEntityList.getList().size() > 0) {
            resAiDrawRecordEntityList.getList().forEach(record -> {
                Task findTask = taskService.getTask(record.getId());
                if (findTask != null) {
                    record.setProgress(findTask.getProgress());
                }
                record.setId(record.getTaskId());
                record.setPicture(putImgUrl(record.getPicture()));
                redisCache.setCacheMapValue(CacheConstants.DRAW_TASK_CACHE+userId, record.getId(),record);
            });
        }
        return resAiDrawRecordEntityList;
    }


    @Override
    public Message deleteTask(String id) {
        Long userId = UserThreadLocal.get().getUserId();
        redisCache.deleteCacheMapValue(CacheConstants.DRAW_TASK_CACHE+userId,id);
        return aiDrawRecordMapper.deleteDrawRecord(id) > 0 ? Message.success() : Message.failure();
    }

    @Override
    public Task getTask(String id) {
        Task task = null;
        Long userId = UserThreadLocal.get().getUserId();

        AiDrawRecordEntity aiDrawRecordEntity = null;
        Task taskJson = redisCache.getCacheMapValue(CacheConstants.DRAW_TASK_CACHE+userId,id);
        if (taskJson != null) {
            task = taskJson;
        } else {
            aiDrawRecordEntity = aiDrawRecordMapper.findDrawRecordEntityByTaskId(id,userId);
            if (aiDrawRecordEntity != null) {
                task = new Task();
                task.setSubmitTime(aiDrawRecordEntity.getSubmitTime());
                task.setFinishTime(aiDrawRecordEntity.getFinishTime());
                task.setAction(aiDrawRecordEntity.getType());
                task.setId(aiDrawRecordEntity.getTaskId());
                task.setStartTime(aiDrawRecordEntity.getStartTime());
                task.setStatus(aiDrawRecordEntity.getStatus());
                task.setDescription(aiDrawRecordEntity.getDescription());
                task.setFailReason(aiDrawRecordEntity.getFailReason());
                task.setPrompt(aiDrawRecordEntity.getPrompt());
                task.setImageUrl(uploadLoadConfig.getQnyImageDomain() + "/" + aiDrawRecordEntity.getMjNewDrawImage());
                task.setPicture(aiDrawRecordEntity.getPicture());
                task.setPromptEn(aiDrawRecordEntity.getPromptEn());
                task.setUserId(aiDrawRecordEntity.getUserId());
                task.setProgress(aiDrawRecordEntity.getProgress());
                task.setRelatedTaskId(aiDrawRecordEntity.getRelatedTaskId());
                task.setMessageHash(aiDrawRecordEntity.getMessageHash());
                task.setNotifyHook(aiDrawRecordEntity.getNotifyHook());
                task.setFinalPrompt(aiDrawRecordEntity.getFinalPrompt());
                task.setLastChannelId(aiDrawRecordEntity.getChannelId());
                task.setKey(aiDrawRecordEntity.getTaskKey());
                task.setMessageId(aiDrawRecordEntity.getMessageId());
                task.setPromptUserRaw(aiDrawRecordEntity.getPromptUserRaw());
                task.setImgHeight(aiDrawRecordEntity.getImgHeight());
                task.setImgWidth(aiDrawRecordEntity.getImgWidth());
                redisCache.setCacheMapValue(CacheConstants.DRAW_TASK_CACHE + userId, task.getId(), task);
            }
        }

        //如果查询的任务正在运行就设置进度
        if (task != null) {
            Task findRunningTask = taskService.getTask(task.getId());
            if (findRunningTask != null) {
                task.setProgress(findRunningTask.getProgress());
            }
            task.setPicture(putImgUrl(task.getPicture()));
            task.setCollectStatus(drawSquareService.drawCollectStatus(task.getId()));
        }

        return task;
    }

    @Override
    public Task getTaskByTaskIdNoUser(String id) {
        Task task = null;
        AiDrawRecordEntity aiDrawRecordEntity = null;
        Task taskJson = redisCache.getCacheMapValue(CacheConstants.DRAW_TASK_CACHE,id);
        if (taskJson != null) {
            task = taskJson;
        }else {
            aiDrawRecordEntity = aiDrawRecordMapper.findDrawRecordEntityByTaskId(id,null);
            if (aiDrawRecordEntity != null) {
                task = new Task();
                task.setFinishTime(aiDrawRecordEntity.getFinishTime());
                task.setAction(aiDrawRecordEntity.getType());
                task.setId(aiDrawRecordEntity.getTaskId());
                task.setStartTime(aiDrawRecordEntity.getStartTime());
                task.setStatus(aiDrawRecordEntity.getStatus());
                task.setDescription(aiDrawRecordEntity.getDescription());
                task.setFailReason(aiDrawRecordEntity.getFailReason());
                task.setPrompt(aiDrawRecordEntity.getPrompt());
                task.setImageUrl(uploadLoadConfig.getQnyImageDomain() + "/" + aiDrawRecordEntity.getMjNewDrawImage());
                task.setPromptEn(aiDrawRecordEntity.getPromptEn());
                task.setUserId(aiDrawRecordEntity.getUserId());
                task.setProgress(aiDrawRecordEntity.getProgress());
                //task.setPicture(aiDrawRecordEntity.getPicture());
                task.setRelatedTaskId(aiDrawRecordEntity.getRelatedTaskId());
                task.setMessageHash(aiDrawRecordEntity.getMessageHash());
                task.setNotifyHook(aiDrawRecordEntity.getNotifyHook());
                task.setFinalPrompt(aiDrawRecordEntity.getFinalPrompt());
                task.setKey(aiDrawRecordEntity.getTaskKey());
                task.setMessageId(aiDrawRecordEntity.getMessageId());
                task.setPromptUserRaw(aiDrawRecordEntity.getPromptUserRaw());
                task.setPicture(putImgUrl(aiDrawRecordEntity.getPicture()));

                redisCache.setCacheMapValue(CacheConstants.DRAW_TASK_CACHE, task.getId(),
                        task);
            }
        }
        return task;
    }

    /**
     * 上传文件到七牛云
     * @param uploadImageDto
     * @return
     */
    @Override
    public Message<String> imageUploadWeb(UploadImageDto uploadImageDto) {
        MultipartFile multipartFile = uploadImageDto.getFile();
        String fileName = multipartFile.getOriginalFilename();
        assert fileName != null;
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        if (!FileUtil.isFileAllowed(suffix)) {
            return Message.failure("不支持的图片类型~");
        }
        String localDir = FileUtil.getAbsolutePath(uploadLoadConfig.getTmpDir());
        String localFile = FileUtil.upload(multipartFile,localDir);
        String filePath = localDir+File.separator+localFile;

        log.debug("开始压缩。。。");
        String densityImgPath = FileUtil.densityImg(filePath,localDir);
        log.debug("压缩完毕。。。{}",densityImgPath);
        if (densityImgPath != null){
            filePath = densityImgPath;
        }
        String upImgUrl = qiniuService.uploadLocalUrl(filePath,"",serverConfig.getUrl(),localDir);

        //String upImgUrl = qiniuService.imageDownNotUpload(filePath,"",);
        if (upImgUrl != null) {
            return  Message.success(putImgUrl(upImgUrl));
        }
        return null;
    }


    /**
     * 七牛云图片转base64
     * @param imgPath
     * @return
     */
    @Override
    public String imageConvertBase64(String imgPath) {
        String filePath = null;
        filePath =FileUtil.getAbsolutePath(uploadLoadConfig.getTmpDir())+File.separator+FileUtil.getFileName(imgPath);
        log.debug("路径：{}  是否存在：{}",filePath,FileUtil.isFileExist(filePath));
        if (!FileUtil.isFileExist(filePath)) {
            DiscordAuthTokenEntity discordAuthTokenEntity = drawKeyManager.getQueueKey();

            log.debug("开始下载文件：{}",imgPath);
            filePath = FileUtil.downRemoteFile(imgPath,FileUtil.getAbsolutePath(uploadLoadConfig.getTmpDir()),
                    "",discordAuthTokenEntity.getProxyHost(),discordAuthTokenEntity.getProxyPort());
            log.debug("下载完毕存入地址：{}",filePath);
        }
        if (FileUtil.isFileExist(filePath)) {
            String base64Str = FileUtil.ImgToBase64(filePath);
            try {
                FileUtil.deleteFile(filePath);
            } catch (IOException e){
                e.printStackTrace();
            }
            return base64Str;
        }
        return null;
    }

    /**
     * 查询提示词
     * @return
     */
    @Override
    public List<AiDrawPromptVo> selectPromptList() {
        List<AiDrawPromptVo> aiDrawPromptVoList = new ArrayList<>();
        List<AiDrawPromptVo> aiDrawPromptVoCacheList = redisCache.getCacheObject(CacheConstants.DRAW_TASK_PROMPT_CACHE);
        if (aiDrawPromptVoCacheList != null && !aiDrawPromptVoCacheList.isEmpty()) {
            return aiDrawPromptVoCacheList;
        }
        List<AiDrawPromptEntity>  aiDrawPromptEntityList= aiDrawPromptMapper.selectDrawPromptCategory();
        if (aiDrawPromptEntityList != null && !aiDrawPromptEntityList.isEmpty()) {
            for (AiDrawPromptEntity va : aiDrawPromptEntityList) {
                AiDrawPromptVo aiDrawPromptVo = new AiDrawPromptVo();
                aiDrawPromptVo.setCategory(va.getCategory());
                List<AiDrawPromptEntity> aiDrawPromptEntityChileList = aiDrawPromptMapper.selectDrawPromptByCategory(
                        va.getCategory());
                if (!aiDrawPromptEntityChileList.isEmpty()) {
                    List<AiDrawPromptCategoryVo> aiDrawPromptCategoryVoArrayList = new ArrayList<>();
                    for (AiDrawPromptEntity val : aiDrawPromptEntityChileList) {
                        AiDrawPromptContentVo aiDrawPromptContentVoContent = new AiDrawPromptContentVo();
                        aiDrawPromptContentVoContent.setKey(val.getDirect());
                        aiDrawPromptContentVoContent.setValue(val.getDepict());
                        AiDrawPromptCategoryVo findOne = aiDrawPromptCategoryVoArrayList.stream().filter(k->k.getCategoryName().equals(val.getClassify())).findAny().orElse(null);
                        if (findOne == null) {
                            AiDrawPromptCategoryVo aiDrawPromptCategoryVo = new AiDrawPromptCategoryVo();
                            aiDrawPromptCategoryVo.setCategoryName(val.getClassify());
                            List<AiDrawPromptContentVo> aiDrawPromptContentVoList = new ArrayList<>();
                            aiDrawPromptContentVoList.add(aiDrawPromptContentVoContent);
                            aiDrawPromptCategoryVo.setCategoryContent(aiDrawPromptContentVoList);
                            aiDrawPromptCategoryVoArrayList.add(aiDrawPromptCategoryVo);
                        }else {
                            aiDrawPromptCategoryVoArrayList.get(aiDrawPromptCategoryVoArrayList.indexOf(findOne)).getCategoryContent().add(aiDrawPromptContentVoContent);
                        }
                    }
                    aiDrawPromptVo.setClassifyList(aiDrawPromptCategoryVoArrayList);
                }
                aiDrawPromptVoList.add(aiDrawPromptVo);
            }
            redisCache.setCacheObject(CacheConstants.DRAW_TASK_PROMPT_CACHE,aiDrawPromptVoList);
        }
        return aiDrawPromptVoList;
    }


    private String putImgUrl(String imgPath) {
        HttpServletRequest request =  ServletUtils.getRequest();
        String originHost = request.getHeader("Hzt-Origin");
        String oriHost = request.getHeader("Host");

        if (StringUtils.isNotEmpty(imgPath)
                && (imgPath.startsWith("/") || imgPath.startsWith("\\"))) {

            String localDomain = StringUtils.isEmpty(originHost) ? "http://"+oriHost : originHost;
            String  domain = uploadLoadConfig.getQnyImageDomain();
            String resImg = "";
            if (StringUtils.isNotEmpty(localDomain) && imgPath.startsWith("/profile")) {
                String localDomainStr = localDomain.endsWith("/") ? localDomain.substring(0,domain.length()-1) : localDomain;
                resImg = localDomainStr+"/api"+imgPath;
            }else if (StringUtils.isNotEmpty(domain)){
                String domainStr = domain.endsWith("/") ? domain.substring(0,domain.length()-1) : domain;
                resImg = domainStr + imgPath;
            }
            return resImg.equals("") ? imgPath : resImg;
        }

        return imgPath;
    }


    @Override
    public void  updatePicture() {
        String aUrl = "https://image.tao62.com/";
        String bUrl = "/";
        List<AiDrawRecordEntity> aiDrawRecordEntityList = aiDrawRecordMapper.vagueSelectDrawRecord(aUrl);
        for (AiDrawRecordEntity a : aiDrawRecordEntityList) {
            if (StringUtils.isNotEmpty(a.getPicture()) && a.getPicture().startsWith(aUrl)) {
                String subUrl = a.getPicture().substring(aUrl.length(),a.getPicture().length());
                String newUrl = bUrl+subUrl;
                AiDrawRecordEntity aiDrawRecordEntity = new AiDrawRecordEntity();
                aiDrawRecordEntity.setPicture(newUrl);
                aiDrawRecordEntity.setTaskId(a.getTaskId());
                aiDrawRecordMapper.updateDrawRecordEntity(aiDrawRecordEntity);
            }
        }
    }

}
