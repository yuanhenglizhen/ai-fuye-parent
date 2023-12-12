package com.hanzaitu.ai.draw.mapper;

import com.hanzaitu.ai.draw.domain.entity.AiDrawCollectEntity;
import com.hanzaitu.ai.draw.domain.entity.AiDrawRecordEntity;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.common.core.HztBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiDrawRecordMapper extends HztBaseMapper<Task> {


    AiDrawRecordEntity findDrawRecordEntityByTaskId(@Param("taskId") String taskId, @Param("userId") Long userId);

    int insertDrawRecordEntity(AiDrawRecordEntity aiDrawRecordEntity);


    int updateDrawRecordEntity(AiDrawRecordEntity aiDrawRecordEntity);



    int deleteDrawRecord(@Param("taskId") String taskId);


    List<AiDrawRecordEntity> selectDrawRecordEntityByUserId(@Param("userId") Long userId);


    List<AiDrawRecordEntity> vagueSelectDrawRecord(@Param("picture") String picture);

}
