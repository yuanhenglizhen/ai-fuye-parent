package com.hanzaitu.ai.chat.mapper;

import com.hanzaitu.common.domain.ChatKeyEntity;
import com.hanzaitu.ai.chat.domain.entity.ChatRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRecordMapper {

    List<ChatRecordEntity> selectChatEntityByUserWinId(@Param("userId") Integer userId,
                                                       @Param("winId") String winId,
                                                       @Param("numberOfContexts") Integer numberOfContexts);

    int insertChatRecord(ChatRecordEntity chatRecordEntity);

    List<ChatRecordEntity> selectChatHistoryTitle(@Param("userId") Integer userId, @Param("model") String model);

    List<ChatKeyEntity> selectChatKeyList();

    int updateDeleteChatHistoryByWinId(@Param("winIdList") List<String> winId,@Param("userId") Integer userId);


    int updateDeleteChatHistoryByUserId(@Param("userId") Integer userId);

    int deleteChatHistory( List<String> winIds);
}
