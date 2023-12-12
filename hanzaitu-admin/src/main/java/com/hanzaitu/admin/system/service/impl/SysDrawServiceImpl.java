package com.hanzaitu.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanzaitu.admin.system.domain.AiDrawQuadratLabelCheck;
import com.hanzaitu.admin.system.domain.AiDrawRecordEntity;
import com.hanzaitu.admin.system.domain.CustomerUser;
import com.hanzaitu.admin.system.domain.dto.DrawCheckDto;
import com.hanzaitu.admin.system.mapper.AiDrawQuadratLabelCheckMapper;
import com.hanzaitu.admin.system.mapper.AiDrawRecordMapper;
import com.hanzaitu.admin.system.mapper.CustomerUserMapper;
import com.hanzaitu.admin.system.service.ISysDrawService;
import com.hanzaitu.admin.utils.SecurityUtils;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.domain.AiDrawQuadratCheckEntity;
import com.hanzaitu.common.domain.AiDrawQuadratEntity;
import com.hanzaitu.common.enums.UserNotifyEnum;
import com.hanzaitu.common.enums.draw.CheckStatusEnum;
import com.hanzaitu.common.enums.draw.DrawStatusEnum;
import com.hanzaitu.common.exception.ServiceException;
import com.hanzaitu.common.mapper.AiDrawQuadratCheckMapper;
import com.hanzaitu.common.mapper.AiDrawQuadratMapper;
import com.hanzaitu.common.service.NotifyMsgCommonService;
import com.hanzaitu.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SysDrawServiceImpl implements ISysDrawService {


    @Autowired
    private AiDrawQuadratCheckMapper aiDrawQuadratCheckMapper;

    @Autowired
    private AiDrawRecordMapper aiDrawRecordMapper;

    @Autowired
    private AiDrawQuadratMapper aiDrawQuadratMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private AiDrawQuadratLabelCheckMapper aiDrawQuadratLabelCheckMapper;


    @Autowired
    private NotifyMsgCommonService notifyMsgCommonService;

    @Override
    @Transactional
    public AjaxResult checkDraw(DrawCheckDto drawCheckDto) {

        if (drawCheckDto.getCheckStatus().toString().equals(CheckStatusEnum.CHECK_COMPLETED.getCode())) {
            AiDrawRecordEntity aiDrawRecordEntity = aiDrawRecordMapper.selectOne(
                    new QueryWrapper<AiDrawRecordEntity>().eq("task_id",drawCheckDto.getTaskId()));

            if (aiDrawRecordEntity == null) {
                return AjaxResult.error("任务不存在，数据异常");
            }

            Long aiDrawQuadratCount = aiDrawQuadratMapper.selectCount(
                    new QueryWrapper<AiDrawQuadratEntity>().eq(
                    "task_id",drawCheckDto.getTaskId()));
            if (aiDrawQuadratCount > 0){
                return AjaxResult.error("此绘画已被发布");
            }

            AiDrawQuadratEntity aiDrawQuadratEntity = new AiDrawQuadratEntity();
            aiDrawQuadratEntity.setTaskId(drawCheckDto.getTaskId());
            aiDrawQuadratEntity.setPrompt(aiDrawRecordEntity.getPromptEn());
            aiDrawQuadratEntity.setImgUrl(aiDrawRecordEntity.getPicture());
            aiDrawQuadratEntity.setStatus(DrawStatusEnum.SHOW.getCode());
            aiDrawQuadratEntity.setWieNum(0);
            aiDrawQuadratEntity.setUserId(aiDrawRecordEntity.getUserId());
            aiDrawQuadratEntity.setImgHeight(aiDrawRecordEntity.getImgHeight());
            aiDrawQuadratEntity.setImgWidth(aiDrawRecordEntity.getImgWidth());

            /*CustomerUser customerUser = customerUserMapper.selectById(aiDrawRecordEntity.getUserId());
            aiDrawQuadratEntity.setUserName(customerUser.getNickName());*/

            AiDrawQuadratCheckEntity aiDrawQuadratCheckData = aiDrawQuadratCheckMapper.selectOne(new QueryWrapper<AiDrawQuadratCheckEntity>().select("create_time")
                    .eq("task_id",drawCheckDto.getTaskId()));
            aiDrawQuadratMapper.insert(aiDrawQuadratEntity);
            updateLabel(drawCheckDto,aiDrawQuadratEntity.getId());
            notifyMsgCommonService.publishUserMessage(aiDrawRecordEntity.getUserId(),
                    String.format(UserNotifyEnum.DRAW_RELEASE_NOTICE.getMsg(),
                    aiDrawQuadratCheckData.getCreateTime()));
        }

        AiDrawQuadratCheckEntity aiDrawQuadratCheckEntity = new AiDrawQuadratCheckEntity();

        aiDrawQuadratCheckEntity.setCheckStatus(CheckStatusEnum.CHECK_COMPLETED.getCode());
        aiDrawQuadratCheckEntity.setCheckUserId(SecurityUtils.getUserId().intValue());
        aiDrawQuadratCheckEntity.setCheckTime(DateUtils.getTime());

        if (aiDrawQuadratCheckMapper.update(aiDrawQuadratCheckEntity,
                new QueryWrapper<AiDrawQuadratCheckEntity>().eq("task_id",drawCheckDto.getTaskId())) == 0) {
            throw new ServiceException("审核失败");
        }

        return  AjaxResult.success();
    }


    /**
     * 插入标签映射
     * @param drawCheckDto
     */
    public void updateLabel(DrawCheckDto drawCheckDto,Long id) {

        aiDrawQuadratLabelCheckMapper.delete(new QueryWrapper<AiDrawQuadratLabelCheck>().eq("task_id",drawCheckDto.getTaskId()));

        List<AiDrawQuadratLabelCheck> aiDrawQuadratLabelCheckList = new ArrayList<AiDrawQuadratLabelCheck>();
        List<String> labelList = Arrays.asList(drawCheckDto.getLabelId().split(","));
        labelList.forEach(s->{
            AiDrawQuadratLabelCheck aiDrawQuadratLabelCheck = new AiDrawQuadratLabelCheck();

            aiDrawQuadratLabelCheck.setQuadratId(id);
            aiDrawQuadratLabelCheck.setLabelId(Integer.parseInt(s));
            aiDrawQuadratLabelCheck.setTaskId(drawCheckDto.getTaskId());
            aiDrawQuadratLabelCheck.setCreateTime(DateUtils.getTime());
            aiDrawQuadratLabelCheck.setUpdateTime(DateUtils.getTime());
            aiDrawQuadratLabelCheckList.add(aiDrawQuadratLabelCheck);
        });

        aiDrawQuadratLabelCheckMapper.saveBatch(aiDrawQuadratLabelCheckList);
    }

}
