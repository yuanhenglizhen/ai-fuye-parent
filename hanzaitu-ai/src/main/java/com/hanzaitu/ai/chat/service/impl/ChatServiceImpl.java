package com.hanzaitu.ai.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.ai.chat.domain.ChatRecord;
import com.hanzaitu.ai.chat.domain.entity.AiChatExpiredConfigEntity;
import com.hanzaitu.ai.chat.domain.entity.AiGptModelEntity;
import com.hanzaitu.ai.chat.domain.entity.ChatRecordEntity;
import com.hanzaitu.ai.chat.domain.entity.WindowInfoEntity;
import com.hanzaitu.ai.chat.domain.vo.ChatHistoryTitleVo;
import com.hanzaitu.ai.chat.listener.GPTEventSourceListener;
import com.hanzaitu.ai.chat.mapper.AiChatExpiredConfigMapper;
import com.hanzaitu.ai.chat.mapper.AiGptModelMapper;
import com.hanzaitu.ai.chat.mapper.ChatRecordMapper;
import com.hanzaitu.ai.chat.service.ChatService;
import com.hanzaitu.common.config.HanZaiTuConfig;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.core.chat.KeyManagerFactory;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.session.UserSession;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.domain.ChatKeyEntity;
import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.hanzaitu.common.enums.RecordTypeEnum;
import com.hanzaitu.common.exception.ServiceException;
import com.hanzaitu.common.manager.AsyncManager;
import com.hanzaitu.common.params.WalletPointParam;
import com.hanzaitu.common.service.FinanceUserWalletService;
import com.hanzaitu.common.utils.DateUtils;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.bean.BeanUtils;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.hanzaitu.common.constant.CacheConstants.*;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private KeyManagerFactory keyManager;

    @Autowired
    private ChatRecordMapper chatRecordMapper;

    @Autowired
    private HanZaiTuConfig hanZaiTuConfig;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AiChatExpiredConfigMapper aiChatExpiredConfigMapper;

    @Autowired
    private AiGptModelMapper aiGptModelMapper;


    @Autowired
    private FinanceUserWalletService financeUserWalletService;

    /**
     * 用户信息
     */
    private UserSession userSession;

    /**
     * 流式返回聊天
     * @param id
     * @param prompt
     * @return
     */
    @Override
    public SseEmitter chatSee(String winId, String msgId, String prompt, String model) {
        SseEmitter sseEmitter = new SseEmitter(-1L);
        WalletPointParam financeUserWallet = financeUserWalletService.expendGptPoints(
                RecordTypeEnum.getEnumTypeByValue(model));
        try {
            userSession = UserThreadLocal.get();
            GptKeyTypeEnum gptKeyTypeEnum = getGptKeyType(model);
            ChatKeyEntity chatKeyEntity = keyManager.getKey(gptKeyTypeEnum);
            if (chatKeyEntity == null) {
                throw new ServiceException("没有可用的Key");
            }
            //获取上下文聊天
            String times = DateUtils.getTime();
            List<Message> messageList = new ArrayList<>();
            List<ChatRecordEntity> reqMsg = getReqMsgList(winId, Message.of(prompt), getGptKeyType(model).name(),
                    msgId, times, 0, model,chatKeyEntity.getNumberOfContexts());
            for (ChatRecordEntity c : reqMsg) {
                if (c.getRole().equals(Message.Role.USER.getValue())) {
                    messageList.add(Message.of(c.getContent()));
                } else if (c.getRole().equals(Message.Role.ASSISTANT.getValue())) {
                    messageList.add(Message.ofAssistant(c.getContent()));
                } else if (c.getRole().equals(Message.Role.SYSTEM.getValue())) {
                    messageList.add(Message.ofSystem(c.getContent()));
                }
            }
            Proxy proxy = Proxy.NO_PROXY;
            //如果不为空就代表需要代理
            if (!StringUtils.isAnyEmpty(chatKeyEntity.getProxyHost(), chatKeyEntity.getProxyHost())) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(chatKeyEntity.getProxyHost(),
                        Integer.parseInt(chatKeyEntity.getProxyPort())));
            }
            log.debug("当前key：{}",chatKeyEntity.getChatKey());

            //获取pid
            Integer pid = 0;
            log.debug("获取到key：" + chatKeyEntity.getChatKey());
            ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                    .apiKey(chatKeyEntity.getChatKey())
                    .proxy(proxy)
                    .timeout(50)
                    .apiHost("https://api.openai.com/")
                    .build()
                    .init();
            GPTEventSourceListener listener = new GPTEventSourceListener(sseEmitter, financeUserWallet,
                    financeUserWalletService, RecordTypeEnum.getEnumTypeByValue(model));
            log.debug("发起请求中");
            ChatCompletion.ChatCompletionBuilder chatCompletionBuilder = ChatCompletion.builder();
            if (chatKeyEntity.getMaxToken() != null) {
                chatCompletionBuilder.maxTokens(chatKeyEntity.getMaxToken());
            }
            chatCompletionBuilder.model(model)
                    .temperature(chatKeyEntity.getTemperature())
                    .messages(messageList)
                    .stream(true);

            ChatCompletion chatCompletion = chatCompletionBuilder.build();

            chatGPTStream.streamChatCompletion(chatCompletion, listener);
            listener.setOnComplate(msg -> {
                addWinTitleCache(winId, prompt, times, gptKeyTypeEnum.name(), model);
                log.debug("消息响应完毕：{}", JacksonUtil.toJsonString(getReqMsgList(winId, Message.ofAssistant(msg),
                        chatCompletion.getModel(), msgId, DateUtils.getTime(), pid,
                        model,chatKeyEntity.getNumberOfContexts())));
            });
        }catch (Exception e) {
            log.error(e.toString());
            //返还积分
            financeUserWalletService.returnGptPoints(RecordTypeEnum.getEnumTypeByValue(model),
                    Math.abs(financeUserWallet.getPoints()),this.userSession.getUserId());
            throw new ServiceException("发送失败了~");
        }
        return sseEmitter;
    }


    /**
     * 获取聊天记录并新增聊天记录
     * @param winId
     * @param message
     * @return
     */
    private List<ChatRecordEntity> getReqMsgList(String winId, Message message, String model,
                                                 String msgId, String times,
                                                 Integer pid, String rawModel, Integer numberOfContexts) {
        Integer userId = userSession.getUserId().intValue();
        String userName = userSession.getUserName();

        //用户id
        String cacheKey = CacheConstants.SESSION_LOG + userId;
        List<ChatRecordEntity> chatRecordEntityList = new ArrayList<>();
        if (message.getRole().equals(Message.Role.USER.getValue())) {
            String recordStr = (String) redisCache.getCacheMapValue(cacheKey, winId);
            if (recordStr != null) {
                chatRecordEntityList = (List<ChatRecordEntity>) JacksonUtil.parseList(recordStr, ChatRecordEntity.class);
                //chatRecordEntityList.sort(Comparator.comparing(ChatRecordEntity::getSort));
                //按照时间排序
                chatRecordEntityList.sort((t1, t2) -> t2.getOccurTime().compareTo(t1.getOccurTime()));
                if (chatRecordEntityList.size() > numberOfContexts) {
                    chatRecordEntityList = chatRecordEntityList.subList(0,numberOfContexts);
                }
            } else {
                chatRecordEntityList = chatRecordMapper.selectChatEntityByUserWinId(userId, winId,
                        numberOfContexts);
            }
        }
        ChatRecordEntity chatRecord = new ChatRecordEntity();
        chatRecord.setWinId(winId);
        chatRecord.setContent(message.getContent());
        chatRecord.setOccurTime(times);
        chatRecord.setUserId(userId);
        chatRecord.setUserName(userName);
        chatRecord.setRole(message.getRole());
        chatRecord.setModel(model);
        if (hanZaiTuConfig.getChatHistoryRetentionTime() != null) {
            chatRecord.setExpiration(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,
                    DateUtils.getSpecifyHoursDate(hanZaiTuConfig.getChatHistoryRetentionTime())));
        }
        //chatRecord.setPid(new BigInteger(String.valueOf(pid)));
        chatRecord.setMsgId(msgId);
        chatRecord.setSubModel(rawModel);
        chatRecordEntityList.add(chatRecord);

        //异步执行记录操作
        AsyncManager.me().execute(chatRecord(cacheKey, winId, message, chatRecord));
        return chatRecordEntityList;
    }




    private void addWinTitleCache(String winId, String content, String date, String model, String rawModel) {
        //用户id
        String cacheKey = CacheConstants.CHAT_WIN_TITLE +model+":"+ userSession.getUserId();
        String recordStr = (String) redisCache.getCacheMapValue(cacheKey,winId);
        if (StringUtils.isEmpty(recordStr)) {
            ChatHistoryTitleVo chatHistoryTitleVo = new ChatHistoryTitleVo();
            chatHistoryTitleVo.setWinTitle(content.length() <= 10 ? content : content.substring(0,10));
            chatHistoryTitleVo.setWinId(winId);
            chatHistoryTitleVo.setCreateTime(date);
            chatHistoryTitleVo.setSubModel(rawModel);
            redisCache.setCacheMapValue(cacheKey,winId,JacksonUtil.toJsonString(chatHistoryTitleVo));
        }
    }

    /**
     * 构建异步处理
     * @param chatRecord
     * @param cacheKey
     * @param winId
     * @param chatRecordEntityList
     * @param message
     * @param times
     * @param model
     * @return
     */
    private TimerTask chatRecord(String cacheKey, String winId, Message message, ChatRecordEntity chatRecord) {
        return new TimerTask() {
            @Override
            public void run()
            {
                try {
                    String occurTime = chatRecord.getOccurTime();
                    String model = chatRecord.getModel();
                    String subMode = chatRecord.getSubModel();

                    List<ChatRecordEntity> chatRecordEntityList = chatRecordMapper.selectChatEntityByUserWinId(
                            chatRecord.getUserId(),winId,null);

                    if (chatRecordEntityList.size() > 0) {
                        int dbSort = chatRecordEntityList.get(chatRecordEntityList.size()-1).getSort() == null ? 0
                                : chatRecordEntityList.get(chatRecordEntityList.size()-1).getSort();

                        chatRecord.setSort(dbSort+1);
                    }
                    if (chatRecordMapper.insertChatRecord(chatRecord) > 0) {
                        chatRecordEntityList.add(chatRecord);
                    }

                    redisCache.setCacheMapValue(cacheKey, winId, JacksonUtil.toJsonString(chatRecordEntityList));
                    //存储用户发送内容
                    if (Objects.equals(message.getRole(), "user")) {
                        addWinTitleCache(winId, message.getContent(), occurTime, model, subMode);
                    }

                    //设置聊天记录过期时间
                    if (Objects.equals(message.getRole(), "assistant")) {
                        //设置窗口过期时间
                        AiChatExpiredConfigEntity aiChatExpiredConfigEntity = getChatExpiredConfig();
                        if (aiChatExpiredConfigEntity != null
                                && aiChatExpiredConfigEntity.getIfOn() == 0 && aiChatExpiredConfigEntity.getChatExpiredDay() > 0) {
                            Date expiredDate = DateUtils.parseDate(DateUtils.getSpecifyDate(aiChatExpiredConfigEntity.getChatExpiredDay()));
                            WindowInfoEntity windowInfoEntity = new WindowInfoEntity();
                            windowInfoEntity.setUserId(userSession.getUserId());
                            windowInfoEntity.setExpiredTime(expiredDate.getTime());
                            windowInfoEntity.setModel(model);
                            redisCache.setCacheMapValue(CHAT_WINDOW_EXPIRED, winId, windowInfoEntity);
                        }
                    }
                }catch (Exception e) {
                    log.error(e.toString());
                }
            }
        };
    }

    /**
     * 查询配置信息
     * @return
     */
    @Override
    public AiChatExpiredConfigEntity getChatExpiredConfig() {
        AiChatExpiredConfigEntity aiChatExpiredConfigEntity = redisCache.getCacheObject(CHAT_EXPIRED_CONFIG_KEY);
        if (aiChatExpiredConfigEntity == null) {
            aiChatExpiredConfigEntity = aiChatExpiredConfigMapper.selectOne(null);
            redisCache.setCacheObject(CHAT_EXPIRED_CONFIG_KEY,aiChatExpiredConfigEntity);
        }
        return aiChatExpiredConfigEntity;
    }


    /**
     * 清理聊天记录
     */
    @Override
    public void cleanUpChatRecord() {
        while (true) {
            try {
                Map<String, WindowInfoEntity> expireChat = redisCache.getCacheMap(CHAT_WINDOW_EXPIRED);
                AiChatExpiredConfigEntity aiChatExpiredConfigEntity = getChatExpiredConfig();
                if (expireChat != null && aiChatExpiredConfigEntity != null) {
                    List<String> winIds = new ArrayList<String>();
                    for (String v : expireChat.keySet()) {
                        WindowInfoEntity dateInfo = expireChat.get(v);
                        long expiredDate = dateInfo.getExpiredTime();
                        long nowDate = DateUtils.getNowDate().getTime();
                        //如果当前设置的开关打开并且有过期的窗口 就删除
                        if (aiChatExpiredConfigEntity.getIfOn() == 0 && nowDate >= expiredDate) {

                            winIds.add(v);
                            log.debug("删除聊天窗口:{},用户Id:{}", v, dateInfo.getUserId());

                            //删除聊天记录
                            redisCache.deleteCacheMapValue(CacheConstants.SESSION_LOG + dateInfo.getUserId(), v);

                            //删除窗口
                            redisCache.deleteCacheMapValue(CacheConstants.CHAT_WIN_TITLE + dateInfo.getModel()
                                    + ":" + dateInfo.getUserId(), v);
                        }
                    }

                    if (!winIds.isEmpty()) {
                        //删除数据库
                        chatRecordMapper.deleteChatHistory(winIds);

                        //删除已执行任务
                        redisCache.deleteCacheMapValueByList(CHAT_WINDOW_EXPIRED,winIds);
                    }
                }
                //30毫秒后再进行检测
                Thread.sleep(30000);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

    /**
     * 查询开启模型
     * @param modelFind
     * @return
     */
    @Override
    public List<AiGptModelEntity> findModelList(GptKeyTypeEnum modelFind) {
        QueryWrapper queryWrapper = new QueryWrapper<AiGptModelEntity>();
        queryWrapper.eq("is_on",'0');
        queryWrapper.eq("type",modelFind);
        List<AiGptModelEntity> aiGptModelEntityList = aiGptModelMapper.selectList(queryWrapper);
        return aiGptModelEntityList;
    }

    /**
     * 根据前缀判断使用的模型大类
     * @param model
     * @return
     */
    private GptKeyTypeEnum getGptKeyType(String model) {
        if (model.trim().startsWith("gpt-3")){
            return GptKeyTypeEnum.GPT_3;
        }else if (model.startsWith("gpt-4")){
            return GptKeyTypeEnum.GPT_4;
        }else {
            return null;
        }
    }

}
