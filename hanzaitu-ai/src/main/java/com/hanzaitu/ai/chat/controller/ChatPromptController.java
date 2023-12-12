package com.hanzaitu.ai.chat.controller;

import com.hanzaitu.ai.chat.domain.dto.AddChatPromptDto;
import com.hanzaitu.ai.chat.domain.dto.ChatCollectPromptDto;
import com.hanzaitu.ai.chat.service.ChatPromptService;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.page.PageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Api(tags = "提示词")
@RestController
@RequestMapping("/chat/prompt")
public class ChatPromptController {

    @Autowired
    private ChatPromptService chatPromptService;

    @ApiOperation(value = "查询提示词列表",httpMethod = "POST")
    @PostMapping("list")
    public AjaxResult getList() {
        return AjaxResult.success(chatPromptService.selectPromptList());
    }

    @ApiOperation(value = "查询收藏列表",httpMethod = "POST")
    @PostMapping("collect-list")
    public AjaxResult getCollectList() {
        return AjaxResult.success(chatPromptService.selectCollectPromptList());
    }

    @ApiOperation(value = "添加收藏",httpMethod = "POST")
    @PostMapping("add-collect")
    public AjaxResult addCollect(@Validated @RequestBody ChatCollectPromptDto chatCollectPromptDto) {
        return chatPromptService.addCollectPrompt(chatCollectPromptDto);
    }


    @ApiOperation(value = "查询自定义提示词列表",httpMethod = "POST")
    @PostMapping("custom-list")
    public AjaxResult getCustomList(PageParam pageParam) {
        return AjaxResult.success(chatPromptService.selectAiPromptCustomList(pageParam));
    }

    @ApiOperation(value = "添加自定义提示词",httpMethod = "POST")
    @PostMapping("custom-add")
    public AjaxResult addCustom(@Validated @RequestBody AddChatPromptDto addChatPromptDto) {
        return AjaxResult.success(chatPromptService.addCustomPrompt(addChatPromptDto));
    }

    @ApiOperation(value = "删除自定义提示词",httpMethod = "POST")
    @PostMapping("custom-del/{id}")
    public AjaxResult delCustom(@NotNull(message = "id不能为空") @PathVariable("id") Long id) {
        return AjaxResult.success(chatPromptService.deleteAiPromptCustom(id));
    }

}
