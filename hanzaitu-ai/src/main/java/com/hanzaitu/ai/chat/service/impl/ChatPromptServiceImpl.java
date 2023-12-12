package com.hanzaitu.ai.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.chat.domain.dto.AddChatPromptDto;
import com.hanzaitu.ai.chat.domain.dto.ChatCollectPromptDto;
import com.hanzaitu.ai.chat.domain.entity.AiCollectPromptEntity;
import com.hanzaitu.ai.chat.domain.entity.AiPromptCategoryEntity;
import com.hanzaitu.ai.chat.domain.entity.AiPromptCustomEntity;
import com.hanzaitu.ai.chat.domain.entity.AiPromptEntity;
import com.hanzaitu.ai.chat.domain.vo.ChatCollectPromptListVo;
import com.hanzaitu.ai.chat.domain.vo.ChatPrompContentVo;
import com.hanzaitu.ai.chat.domain.vo.ChatPromptListVo;
import com.hanzaitu.ai.chat.mapper.AiPromptCustomMapper;
import com.hanzaitu.ai.chat.mapper.ChatPromptMapper;
import com.hanzaitu.ai.chat.service.ChatPromptService;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.PageParam;
import com.hanzaitu.common.utils.DateUtils;
import com.hanzaitu.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatPromptServiceImpl extends HztBaseService implements ChatPromptService {

    @Autowired
    private ChatPromptMapper chatPromptMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AiPromptCustomMapper aiPromptCustomMapper;

    @Override
    public List<ChatPromptListVo> selectPromptList() {
        List<ChatPromptListVo> resList = new ArrayList<>();
        List<ChatPromptListVo> cacheResList = redisCache.getCacheObject(CacheConstants.CHAT_PROMPT);
        if (cacheResList!=null && !cacheResList.isEmpty()) {
            resList = cacheResList;
        }
        if (resList == null || resList.isEmpty()) {
            List<AiPromptCategoryEntity> aiPromptCategoryEntities = chatPromptMapper.selectPromptCategory();
            if (aiPromptCategoryEntities != null && !aiPromptCategoryEntities.isEmpty()) {
                for (AiPromptCategoryEntity a : aiPromptCategoryEntities) {
                    ChatPromptListVo chatPromptListVo = new ChatPromptListVo();
                    chatPromptListVo.setCategoryId(a.getId());
                    chatPromptListVo.setCategoryName(a.getCategoryName());
                    List<ChatPrompContentVo> chatPrompContentVoList = new ArrayList<>();
                    List<AiPromptEntity> aiPromptEntityList = chatPromptMapper.selectPrompt(a.getId());
                    for (AiPromptEntity v : aiPromptEntityList) {
                        ChatPrompContentVo chatPrompContentVo = new ChatPrompContentVo();
                        chatPrompContentVo.setContent(v.getPrompt());
                        chatPrompContentVo.setPrompId(v.getId());
                        chatPrompContentVo.setTitle(v.getRole());
                        chatPrompContentVoList.add(chatPrompContentVo);
                    }
                    chatPromptListVo.setPromptList(chatPrompContentVoList);
                    resList.add(chatPromptListVo);
                }
                redisCache.setCacheObject(CacheConstants.CHAT_PROMPT, resList);
            }
        }
        return resList;
    }

    @Override
    public List<ChatCollectPromptListVo> selectCollectPromptList() {
        Integer userId = UserThreadLocal.get().getUserId().intValue();
        List<ChatCollectPromptListVo> resList = new ArrayList<>();

        List<AiCollectPromptEntity> aiPromptCategoryEntities = chatPromptMapper.selectCollectPrompt(userId);
        for (AiCollectPromptEntity a : aiPromptCategoryEntities) {
            ChatCollectPromptListVo chatCollectPromptListVo = new ChatCollectPromptListVo();
            chatCollectPromptListVo.setCollectId(a.getId());
            chatCollectPromptListVo.setCreateTime(a.getCreateTime());
            chatCollectPromptListVo.setContent(a.getPrompt());
            chatCollectPromptListVo.setMsgId(a.getMsgId());
            resList.add(chatCollectPromptListVo);
        }
        return resList;
    }

    @Override
    public AjaxResult addCollectPrompt(ChatCollectPromptDto chatCollectPromptDto) {
        Integer userId = UserThreadLocal.get().getUserId().intValue();

        if (chatCollectPromptDto.getOperate().equals(ChatCollectPromptDto.ChatCollectOperate.ADD.getValue())){
            if (chatPromptMapper.selectCollectPromptByMsgId(chatCollectPromptDto.getMsgId()) != null){
                return AjaxResult.error("不能重复收藏");
            }
            if (StringUtils.isEmpty(chatCollectPromptDto.getContent())) {
                return AjaxResult.error("消息内容不能为空");
            }
            AiCollectPromptEntity aiCollectPromptEntity = new AiCollectPromptEntity();
            aiCollectPromptEntity.setPrompt(chatCollectPromptDto.getContent());
            aiCollectPromptEntity.setMsgId(chatCollectPromptDto.getMsgId());
            aiCollectPromptEntity.setCreateTime(DateUtils.getTime());
            aiCollectPromptEntity.setUserId(userId);
            chatPromptMapper.insertCollectPrompt(aiCollectPromptEntity);
        }else if (chatCollectPromptDto.getOperate().equals(ChatCollectPromptDto.ChatCollectOperate.DEL.getValue())){
            if (chatPromptMapper.selectCollectPromptByMsgId(chatCollectPromptDto.getMsgId()) == null){
                return AjaxResult.error("此数据未收藏");
            }
            chatPromptMapper.deleteCollectPrompt(chatCollectPromptDto.getMsgId());
        }

        return AjaxResult.success();
    }


    /**
     * 添加自定义提示词
     * @param addChatPromptDto
     * @return
     */
    @Override
    public AjaxResult addCustomPrompt(AddChatPromptDto addChatPromptDto) {
        AiPromptCustomEntity aiPromptCustomEntity = new AiPromptCustomEntity();
        BeanUtils.copyProperties(addChatPromptDto,aiPromptCustomEntity);
        aiPromptCustomEntity.setUserId(getUserSession().getUserId());
        return aiPromptCustomMapper.insert(aiPromptCustomEntity) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 查询自定义提示词列表
     * @param pageParam
     * @return
     */
    @Override
    public HztPage<AiPromptCustomEntity> selectAiPromptCustomList(PageParam pageParam) {

        return aiPromptCustomMapper.selectPage(pageParam,
                new QueryWrapper<AiPromptCustomEntity>().eq("user_id",getUserSession().getUserId()));
    }


    /**
     * 删除自定义提示词
     * @param pageParam
     * @return
     */
    @Override
    public AjaxResult deleteAiPromptCustom(Long id) {
        return aiPromptCustomMapper.delete(new QueryWrapper<AiPromptCustomEntity>()
                .eq("id",id).eq("user_id",getUserSession().getUserId()))
                > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
