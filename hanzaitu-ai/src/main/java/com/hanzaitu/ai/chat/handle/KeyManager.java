package com.hanzaitu.ai.chat.handle;

import com.hanzaitu.ai.chat.mapper.ChatRecordMapper;
import com.hanzaitu.ai.chat.service.ChatService;
import com.hanzaitu.common.core.SystemInfo;
import com.hanzaitu.common.core.chat.KeyManagerFactory;
import com.hanzaitu.common.core.mybatis.SqlScript;
import com.hanzaitu.common.manager.AsyncManager;
import com.hanzaitu.common.utils.SpringApplicationUtils;
import com.hanzaitu.common.utils.chat.CircularBlockingQueue;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.core.executor.ExecutorConfig;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.domain.ChatKeyEntity;
import com.hanzaitu.common.domain.ChatKeyRedisList;
import com.hanzaitu.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * gpt Key队列管理
 */
@Slf4j
@Service
@Order(1)
public class KeyManager implements ApplicationRunner {

    @Resource
    private ChatRecordMapper chatRecordMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * redis监听超时时间
     */
    private final Long cacheTimeOut = 30L;

    @Autowired
    private ChatService chatService;

    @Autowired
    private KeyManagerFactory keyManagerFactory;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SystemInfo systemInfo;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {

            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    //查询数据库的key
                    selectStoreKeyList();
                    //监听是否有新的key加入
                    queueRedisAction();
                }
            });
            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    //清除过期聊天记录
                    chatService.cleanUpChatRecord();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateSql();
    }

    /**
     * 初始化数据库中的 key
     */
    private void selectStoreKeyList() {
        List<ChatKeyEntity> chatKeyEntities = chatRecordMapper.selectChatKeyList();
        if (chatKeyEntities != null && !chatKeyEntities.isEmpty()) {
            for (ChatKeyEntity v : chatKeyEntities) {
                keyManagerFactory.addQueue(v);
            }
            log.debug("初始化GPT " + chatKeyEntities.size() + "个配置的KEY");
        }
    }

    /**
     * 监听redis队列
     */
    private void queueRedisAction() {
        while (true){
            try {
                ChatKeyRedisList chatKeyEntity = (ChatKeyRedisList) redisCache.brpop(CacheConstants.CHAT_QUEUE_KEY,
                        cacheTimeOut, TimeUnit.MINUTES);
                if (chatKeyEntity != null && chatKeyEntity.getData() != null) {
                    switch (chatKeyEntity.getKeyManagerRedisOperationType()) {
                        case ADD:
                            keyManagerFactory.addQueue(chatKeyEntity.getData());
                            break;
                        case DELETE:
                            keyManagerFactory.delQueue(chatKeyEntity.getData());
                            break;
                        case UPDATE:
                            keyManagerFactory.delQueue(chatKeyEntity.getData());
                            keyManagerFactory.addQueue(chatKeyEntity.getData());
                            break;
                    }
                    log.debug("redis队列收到操作类型{}  模型{} key:{}",chatKeyEntity.getKeyManagerRedisOperationType(),
                            chatKeyEntity.getData().getModel(),chatKeyEntity.getData().getChatKey());
                }
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void updateSql(){
        if(SpringApplicationUtils.getDevMode()){
            log.info("执行sql");
            SqlScript sqlScript = new SqlScript(systemInfo, jdbcTemplate);
            sqlScript.execCurrentVersion();
        }
    }
}
