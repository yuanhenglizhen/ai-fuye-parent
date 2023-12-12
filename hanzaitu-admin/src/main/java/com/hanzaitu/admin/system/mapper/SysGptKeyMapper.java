package com.hanzaitu.admin.system.mapper;

import com.hanzaitu.admin.system.domain.GptKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysGptKeyMapper {

    /**
     * 插入
     * @param gptKey
     * @return
     */
    int insertGptKey(GptKey gptKey);


    List<GptKey> selectGptKeyList();


    int updateGptKey(GptKey gptKey);


    int deleteGptKey(@Param("id") Integer id);


    GptKey findOneGptKey(@Param("chatKey") String key);

}
