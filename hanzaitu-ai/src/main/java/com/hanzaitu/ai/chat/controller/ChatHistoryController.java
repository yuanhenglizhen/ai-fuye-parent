package com.hanzaitu.ai.chat.controller;

import com.hanzaitu.ai.chat.domain.dto.ChatHistoryContentDto;
import com.hanzaitu.ai.chat.domain.dto.ChatHistoryDto;
import com.hanzaitu.ai.chat.domain.vo.ChatHistoryTitleVo;
import com.hanzaitu.ai.chat.service.ChatRecordService;
import com.hanzaitu.common.core.controller.BaseController;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.hanzaitu.common.utils.StringUtils;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api(tags = "聊天数据")
@RestController
@RequestMapping("/chat/history")
public class ChatHistoryController extends BaseController {

    @Autowired
    private ChatRecordService chatRecordService;

    @ApiOperation(value = "查询窗口列表",httpMethod = "POST")
    @PostMapping("title-list")
    public AjaxResult getTitleList() {
        List<ChatHistoryTitleVo> list = chatRecordService.selectHistoryChatTitle(GptKeyTypeEnum.GPT_3.name());
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "gpt4-查询窗口列表",httpMethod = "POST")
    @PostMapping("gpt4-title-list")
    public AjaxResult getGpt4TitleList() {
        List<ChatHistoryTitleVo> list = chatRecordService.selectHistoryChatTitle(GptKeyTypeEnum.GPT_4.name());
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "删除窗口",httpMethod = "POST")
    @PostMapping("del-win")
    public AjaxResult delWin(@Validated @RequestBody @ApiParam(name="传入对象",value="传入json格式",required=true)
                                 ChatHistoryDto chatHistoryDto) {
        if(chatHistoryDto.getOperate().equals("choose") && StringUtils.isEmpty(chatHistoryDto.getWinId())) {
            return AjaxResult.error("winId不能为空");
        }
        chatRecordService.updateDeleteHistoryChatTitle(chatHistoryDto);
        return AjaxResult.success();
    }

    @PostMapping("content")
    @ApiOperation(value = "聊天记录",httpMethod = "POST")
    public AjaxResult getContentList(@Validated @RequestBody ChatHistoryContentDto ChatHistoryContentDto) {
        return AjaxResult.success(chatRecordService.selectHistoryChatContent(ChatHistoryContentDto));
    }

}
