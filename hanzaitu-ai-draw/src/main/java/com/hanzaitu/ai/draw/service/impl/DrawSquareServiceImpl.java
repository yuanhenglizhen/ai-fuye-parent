package com.hanzaitu.ai.draw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.domain.dto.DrawQuadratDto;
import com.hanzaitu.ai.draw.domain.dto.DrawQuadratPraiseDto;
import com.hanzaitu.ai.draw.domain.entity.AiDrawCollectEntity;
import com.hanzaitu.ai.draw.domain.entity.AiDrawQuadratLabelEntity;
import com.hanzaitu.ai.draw.domain.entity.AiDrawQuadratPraiseEntity;
import com.hanzaitu.ai.draw.domain.vo.AiDrawRraiseListVo;
import com.hanzaitu.ai.draw.mapper.AiDrawCollectionMapper;
import com.hanzaitu.ai.draw.mapper.AiDrawQuadratLabelMapper;
import com.hanzaitu.ai.draw.mapper.AiDrawQuadratPraiseMapper;
import com.hanzaitu.ai.draw.service.DrawSquareService;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.mybatis.MybatisPage;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.session.UserSession;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.AiDrawQuadratEntity;
import com.hanzaitu.common.mapper.AiDrawQuadratMapper;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.PageParam;
import com.hanzaitu.common.utils.ServletUtils;
import com.hanzaitu.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DrawSquareServiceImpl implements DrawSquareService {

    @Autowired
    private AiDrawQuadratMapper aiDrawQuadratMapper;

    @Autowired
    private AiDrawQuadratPraiseMapper aiDrawQuadratPraiseMapper;

    @Autowired
    private AiDrawQuadratLabelMapper aiDrawQuadratLabelMapper;

    @Autowired
    private AiDrawCollectionMapper aiDrawCollectionMapper;


    @Autowired
    private UploadLoadConfig uploadLoadConfig;


    @Autowired
    private RedisCache redisCache;

    @Resource
    private RedissonClient redisson;

    /**
     * 查询所有标签
     * @return
     */
    @Override
    public AjaxResult selectLabel() {

        List<AiDrawQuadratLabelEntity> aiDrawQuadratLabelEntityList= aiDrawQuadratLabelMapper.selectList(
                new QueryWrapper<AiDrawQuadratLabelEntity>().orderBy(true,true,"sort"));

        return AjaxResult.success(aiDrawQuadratLabelEntityList);
    }


    /**
     * 查询绘画广场列表
     * @param drawQuadratDto
     * @return
     */
    @Override
    public HztPage<AiDrawQuadratEntity> selectList(DrawQuadratDto drawQuadratDto) {

        HztPage<AiDrawQuadratEntity>  hztPageData= aiDrawQuadratMapper.selectDrawQuadratList(
                MybatisPage.convert(drawQuadratDto),
                drawQuadratDto.getLabelId(),drawQuadratDto.getSortType().toString()).convert();
        List<String> praiseList;
        UserSession userSession = UserThreadLocal.get();
        if (userSession != null) {
            Long userId = userSession.getUserId();
            praiseList = getRraiseList(userId);
        } else {
            praiseList = null;
        }
        if (!hztPageData.getList().isEmpty()) {
            hztPageData.getList().forEach(record -> {
                if (praiseList != null) {
                    record.setSquareStatus(praiseList.contains(record.getTaskId()));
                }
                record.setImgUrl(putImgUrl(record.getImgUrl()));
            });
        }
        return hztPageData;
    }

    /**
     * 创作收藏绘画列表
     * @param pageParam
     * @return
     */
    @Override
    public HztPage<AiDrawCollectEntity> collectDrawList(PageParam pageParam) {
        Long userId = UserThreadLocal.get().getUserId();

        HztPage<AiDrawCollectEntity> aiDrawCollectEntityHztPageData = aiDrawCollectionMapper.selectDrawCollectList(
                MybatisPage.convert(pageParam),userId).convert();

        List<String> praiseList = getRraiseList(userId);
        if (!aiDrawCollectEntityHztPageData.getList().isEmpty()) {
            aiDrawCollectEntityHztPageData.getList().forEach(record -> {
                if (praiseList != null) {
                    record.setSquareStatus(praiseList.contains(record.getTaskId()));
                }
                record.setImgUrl(putImgUrl(record.getImgUrl()));
            });
        }
        return aiDrawCollectEntityHztPageData;
    }

    /**
     * 是否被收藏
     * @param taskId
     * @return
     */
    @Override
    public Boolean drawCollectStatus(String taskId) {

        return aiDrawCollectionMapper.selectCount(new QueryWrapper<AiDrawCollectEntity>().eq("task_id",taskId)) > 0;
    }


    /**``
     * 查询点赞收藏列表
     * @param pageParam
     * @return
     */
    @Override
    public HztPage<AiDrawRraiseListVo> aiDrawRraiseList(PageParam pageParam) {
        Long userId = UserThreadLocal.get().getUserId();
        HztPage<AiDrawRraiseListVo> aiDrawRraiseListVoHztPageData = aiDrawQuadratPraiseMapper
                .selectDrawRraiseList(MybatisPage.convert(pageParam),userId).convert();

        if (!aiDrawRraiseListVoHztPageData.getList().isEmpty()) {
            aiDrawRraiseListVoHztPageData.getList().forEach(record -> {
                record.setSquareStatus(true);
                record.setImgUrl(putImgUrl(record.getImgUrl()));
            });
        }
        return aiDrawRraiseListVoHztPageData;
    }

    /**
     * 绘画点赞
     * @param drawQuadratPraiseDto
     * @return
     */
    @Override
    @Transactional
    public AjaxResult praise(DrawQuadratPraiseDto drawQuadratPraiseDto) {
        Long userId = UserThreadLocal.get().getUserId();
        RLock lock = redisson.getLock(drawQuadratPraiseDto.getTaskId());       //获取锁
        try {
            lock.lock();    //加锁
            String praiseList = redisCache.getCacheMapValue(CacheConstants.DRAW_PRAISE, userId.toString());
            boolean isAdd = true;
            List<String> taskIdList = new ArrayList<>();
            if (StringUtils.isNotEmpty(praiseList)) {
                String[] taskId = praiseList.split(",");
                //直接asList出来的List无法进行remove操作
                taskIdList = new ArrayList<>(Arrays.asList(taskId));
                //包含说明这个任务被点赞过
                if (StringUtils.containsAnyEqIgnoreCase(drawQuadratPraiseDto.getTaskId(), taskId)) {
                    taskIdList.remove(drawQuadratPraiseDto.getTaskId());
                    //点赞数-1
                    isAdd = false;
                } else {
                    //点赞数+1
                    taskIdList.add(drawQuadratPraiseDto.getTaskId());
                }
            } else {
                List<AiDrawQuadratPraiseEntity> aiDrawQuadratPraiseList = aiDrawQuadratPraiseMapper.selectList(
                        new QueryWrapper<AiDrawQuadratPraiseEntity>().eq("user_id", userId));
                if (aiDrawQuadratPraiseList != null && !aiDrawQuadratPraiseList.isEmpty()) {
                    taskIdList = aiDrawQuadratPraiseList.stream().map(AiDrawQuadratPraiseEntity::getTaskId)
                            .collect(Collectors.toList());
                    if (taskIdList.contains(drawQuadratPraiseDto.getTaskId())) {
                        taskIdList.remove(drawQuadratPraiseDto.getTaskId());
                        //点赞数-1
                        isAdd = false;
                    } else {
                        taskIdList.add(drawQuadratPraiseDto.getTaskId());
                        //点赞数+1
                    }
                } else {
                    taskIdList.add(drawQuadratPraiseDto.getTaskId());
                    //点赞数+1
                }
            }
            updatePraiseNum(userId, drawQuadratPraiseDto.getTaskId(), isAdd, taskIdList);
            return AjaxResult.success(isAdd ? "点赞成功" : "取消点赞成功").put("praiseStatus",isAdd);
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        } finally {
            lock.unlock();
            log.debug("已解锁");
        }
        return AjaxResult.error("操作失败");
    }

    /**
     * 更新点赞数
     * @param taskId
     * @param isAdd   true 新增点赞数， false取消点赞
     * @return
     */
    private boolean updatePraiseNum(Long userId, String taskId, Boolean isAdd,List<String> taskIdList) {

        try {

            if (isAdd) {
                AiDrawQuadratPraiseEntity aiDrawQuadratPraiseEntity = new AiDrawQuadratPraiseEntity();
                aiDrawQuadratPraiseEntity.setUserId(userId);
                aiDrawQuadratPraiseEntity.setTaskId(taskId);
                aiDrawQuadratPraiseMapper.insert(aiDrawQuadratPraiseEntity);
            } else {
                aiDrawQuadratPraiseMapper.delete(new QueryWrapper<AiDrawQuadratPraiseEntity>()
                        .eq("user_id", userId).eq("task_id", taskId));
            }
            UpdateWrapper wrapper = new UpdateWrapper();
            wrapper.eq("task_id",taskId);
            wrapper.setSql(String.format("`wie_num` = wie_num %s",isAdd ? "+ 1" : "- 1"));
            aiDrawQuadratMapper.update(new AiDrawQuadratEntity(),wrapper);
            redisCache.setCacheMapValue(CacheConstants.DRAW_PRAISE, userId.toString(), String.join(",", taskIdList));
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    private List<String> getRraiseList(Long userId) {
        String praiseList = redisCache.getCacheMapValue(CacheConstants.DRAW_PRAISE,userId.toString());
        if (StringUtils.isNotEmpty(praiseList)) {
            return new ArrayList<>(Arrays.asList(praiseList.split(",")));
        }else {
            QueryWrapper<AiDrawQuadratPraiseEntity> queryWrapper = new QueryWrapper<AiDrawQuadratPraiseEntity>();
            queryWrapper.select("task_id");
            queryWrapper.eq("user_id",userId);
            List<AiDrawQuadratPraiseEntity> aiDrawQuadratPraiseEntityList = aiDrawQuadratPraiseMapper.selectList(queryWrapper);
            if (aiDrawQuadratPraiseEntityList != null && !aiDrawQuadratPraiseEntityList.isEmpty()) {
                List<String> taskId = aiDrawQuadratPraiseEntityList.stream().map(
                        AiDrawQuadratPraiseEntity::getTaskId).collect(Collectors.toList());
                redisCache.setCacheMapValue(CacheConstants.DRAW_PRAISE,userId.toString(),String.join(",",taskId));
                return taskId;
            }
            return null;
        }
    }

    private String putImgUrl(String imgPath) {
        if (StringUtils.isNotEmpty(imgPath) && (imgPath.startsWith("/") || imgPath.startsWith("\\"))) {
            String  domain = uploadLoadConfig.getQnyImageDomain();
            HttpServletRequest request =  ServletUtils.getRequest();
            String originHost = request.getHeader("Hzt-Origin");
            String oriHost = request.getHeader("Host");
            String localDomain = StringUtils.isEmpty(originHost) ? "http://"+oriHost : originHost;
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
}
