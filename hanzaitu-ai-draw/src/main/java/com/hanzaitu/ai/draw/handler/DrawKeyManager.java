package com.hanzaitu.ai.draw.handler;

import com.hanzaitu.ai.draw.domain.entity.DiscordAuthTokenEntity;
import com.hanzaitu.ai.draw.mapper.AiDrawKeyMapper;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.chat.CircularBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DrawKeyManager {

    @Autowired
    private AiDrawKeyMapper aiDrawKeyMapper;

    /**
     * 读取mj配置
     */
    private List<DiscordAuthTokenEntity> discordAuthTokenEntityList;

    /**
     * 核心数量
     */
    private Integer coreInt = 0;


    /**
     * 环形队列，用于并发获取key
     */
    private static CircularBlockingQueue<DiscordAuthTokenEntity> keyQueue = new CircularBlockingQueue<DiscordAuthTokenEntity>();

    /**
     * 队列获取key
     * @return
     */
    public synchronized DiscordAuthTokenEntity getQueueKey() {
        DiscordAuthTokenEntity next = keyQueue.next();
        return next;
    }

    /**
     * 查询并行数量
     * @return
     */
    public Integer getCoreInt() {
        return coreInt;
    }



    public List<DiscordAuthTokenEntity> getDiscordAuthTokenEntityList() {
        return discordAuthTokenEntityList;
    }

    /**
     * 初始化key
     * @param args incoming application arguments
     * @throws Exception
     */
    //@Override
    public void run () {
        List<DiscordAuthTokenEntity> authTokenEntityResList = aiDrawKeyMapper.selectDrawKeyList();
        if (authTokenEntityResList != null && !authTokenEntityResList.isEmpty()) {
            discordAuthTokenEntityList = authTokenEntityResList;
            for (DiscordAuthTokenEntity  d : authTokenEntityResList) {
                if(StringUtils.isAnyEmpty(d.getChannelId(),d.getGuildId(),d.getBotToken(),d.getUserToken())
                        || d.getCoreSize() == null) {
                    throw new RuntimeException("缺少discord必要参数,请检查配置，启动失败");
                }
                keyQueue.add(d);
                coreInt += d.getCoreSize();
            }
            log.debug("draw 初始化到 {} 个key",authTokenEntityResList.size());
        }
    }
}
