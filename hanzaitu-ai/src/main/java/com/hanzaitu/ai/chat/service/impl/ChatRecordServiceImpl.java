package com.hanzaitu.ai.chat.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hanzaitu.ai.chat.domain.dto.ChatHistoryContentDto;
import com.hanzaitu.ai.chat.domain.dto.ChatHistoryDto;
import com.hanzaitu.ai.chat.domain.entity.AiCollectPromptEntity;
import com.hanzaitu.ai.chat.domain.entity.ChatRecordEntity;
import com.hanzaitu.ai.chat.domain.vo.ChatHistoryContentVo;
import com.hanzaitu.ai.chat.domain.vo.ChatHistoryTitleVo;
import com.hanzaitu.ai.chat.mapper.ChatPromptMapper;
import com.hanzaitu.ai.chat.mapper.ChatRecordMapper;
import com.hanzaitu.ai.chat.service.ChatRecordService;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatRecordServiceImpl implements ChatRecordService {

    @Autowired
    private ChatRecordMapper chatRecordMapper;

    @Autowired
    private ChatPromptMapper chatPromptMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public List<ChatHistoryTitleVo> selectHistoryChatTitle(String model) {
        int userId = UserThreadLocal.get().getUserId().intValue(),cutLen = 30;
        List<ChatHistoryTitleVo> chatHistoryTitleVoList = new ArrayList<>();
        String cacheKey = CacheConstants.CHAT_WIN_TITLE +model+":"+  userId;
        List<T> recordStr = redisCache.getAllCacheMapValue(cacheKey);
        if (recordStr != null && !recordStr.isEmpty()) {
            for (Object titleV : recordStr) {
                ChatHistoryTitleVo chatHistoryTitleVo=
                        JacksonUtil.parseObject((String) titleV,ChatHistoryTitleVo.class);
                chatHistoryTitleVoList.add(chatHistoryTitleVo);
            }
            return chatHistoryTitleVoList;
        }

        List<ChatRecordEntity> chatRecordEntityList = chatRecordMapper.selectChatHistoryTitle(userId,model);
        if (StringUtils.isNotEmpty(chatRecordEntityList)) {
            for (ChatRecordEntity c : chatRecordEntityList) {
                ChatHistoryTitleVo chatHistoryTitleVo = new ChatHistoryTitleVo();
                String title = c.getContent().length() <= cutLen ? c.getContent() : c.getContent().substring(0,cutLen);
                chatHistoryTitleVo.setWinTitle(title);
                chatHistoryTitleVo.setWinId(c.getWinId());
                chatHistoryTitleVo.setCreateTime(c.getOccurTime());
                chatHistoryTitleVo.setSubModel(c.getSubModel());
                if (c.getWinId() == null) {
                    continue;
                }
                redisCache.setCacheMapValue(cacheKey,c.getWinId(),JacksonUtil.toJsonString(chatHistoryTitleVo));
                chatHistoryTitleVoList.add(chatHistoryTitleVo);
            }
        }
        return chatHistoryTitleVoList;
    }

    @Async
    @Override
    public void updateDeleteHistoryChatTitle(ChatHistoryDto chatHistoryDto) {
        Integer userId = UserThreadLocal.get().getUserId().intValue();
        String operate = chatHistoryDto.getOperate();
        String model = chatHistoryDto.getModel().name();
        if (operate.equals("choose")) {
            String winIdList = chatHistoryDto.getWinId();
            List<String> winList = Arrays.asList(winIdList.split(","));
            chatRecordMapper.updateDeleteChatHistoryByWinId(winList,userId);
            redisCache.deleteCacheMapValueByList(CacheConstants.SESSION_LOG+userId,winList);
            redisCache.deleteCacheMapValueByList(CacheConstants.CHAT_WIN_TITLE+model+":"+userId,winList);
        } else if (operate.equals("clean")){
            chatRecordMapper.updateDeleteChatHistoryByUserId(userId);
            redisCache.deleteObject(CacheConstants.SESSION_LOG+userId);
            redisCache.deleteObject(CacheConstants.CHAT_WIN_TITLE+model+":"+userId);
        }
    }

    @Override
    public List<ChatHistoryContentVo> selectHistoryChatContent(ChatHistoryContentDto ChatHistoryContentDto) {
        int userId = UserThreadLocal.get().getUserId().intValue(),cutLen = 10;
        String winId = ChatHistoryContentDto.getWinId();
        List<ChatRecordEntity> chatRecordEntityList = new ArrayList<>();
        List<ChatHistoryContentVo> chatHistoryContentVoList = new ArrayList<>();
        String recordStr = (String) redisCache.getCacheMapValue(CacheConstants.SESSION_LOG+userId,winId);
        if(recordStr != null) {
            chatRecordEntityList = (List<ChatRecordEntity>) JacksonUtil.parseList(recordStr, ChatRecordEntity.class);

        }else {
            chatRecordEntityList = chatRecordMapper.selectChatEntityByUserWinId(userId,winId,null);
        }
        if (chatRecordEntityList != null && !chatRecordEntityList.isEmpty()) {
            for (ChatRecordEntity cRty : chatRecordEntityList) {
                ChatHistoryContentVo chatHistoryContentVo = new ChatHistoryContentVo();
                chatHistoryContentVo.setContent(cRty.getContent());
                chatHistoryContentVo.setWinId(cRty.getWinId());
                chatHistoryContentVo.setOccurTime(cRty.getOccurTime());
                chatHistoryContentVo.setMsgId(cRty.getMsgId());
                chatHistoryContentVo.setSubModel(cRty.getSubModel());
                String role = cRty.getRole();
                chatHistoryContentVo.setRole(role);
                AiCollectPromptEntity aiCollectPromptEntity = chatPromptMapper.selectCollectPromptByMsgId(cRty.getMsgId());
                if (aiCollectPromptEntity != null) {
                    chatHistoryContentVo.setCollectId(String.valueOf(aiCollectPromptEntity.getId()));
                }
                chatHistoryContentVoList.add(chatHistoryContentVo);
            }
            chatHistoryContentVoList = chatHistoryContentVoList.stream().sorted(new Comparator<ChatHistoryContentVo>() {
                @Override
                public int compare(ChatHistoryContentVo m1, ChatHistoryContentVo m2) {
                    try {
                        DateTime d1 = DateUtil.parse((String) m1.getOccurTime());
                        DateTime d2 = DateUtil.parse((String) m2.getOccurTime());
                        //正序
                        return d1.compareTo(d2);

                        //倒序
                        //return d2.compareTo(d1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            }).collect(Collectors.toList());
        }

        return chatHistoryContentVoList;
    }

}
