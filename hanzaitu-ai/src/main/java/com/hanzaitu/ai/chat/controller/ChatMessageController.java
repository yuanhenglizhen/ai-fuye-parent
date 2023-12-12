package com.hanzaitu.ai.chat.controller;


import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.ai.chat.domain.dto.ChatMsgDto;
import com.hanzaitu.ai.chat.service.ChatService;
import com.hanzaitu.common.annotation.ReqLimit;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.hanzaitu.common.exception.ServiceException;
import com.hanzaitu.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.annotation.Resource;

@Api(tags = "聊天操作")
@RestController
@RequestMapping("/chat")
public class ChatMessageController {

    @Resource
    private ChatService chatGptService;

    /**
     * 流式聊天
     * @param winId
     * @param msgId
     * @param prompt
     * @return
     */
    @CrossOrigin
    @GetMapping("sse")
    @ReqLimit(permitsPerSecond = 10)
    @ApiOperation(value = "gpt 发起聊天(数据流式返回)",httpMethod = "GET")
    public SseEmitter sendMsg(@ApiParam(name="winId",value="窗口Id",required=true) String winId,
                              @ApiParam(name="msgId",value="消息Id",required=true) String msgId,
                              @ApiParam(name="model",value = "使用模型",required=true) String model,
                              @ApiParam(name="prompt",value="聊天内容",required=true) String prompt){
        if (StringUtils.isAnyEmpty(winId,msgId,prompt)) {
            throw new ServiceException("参数不能为空！！");
        }
        return chatGptService.chatSee(winId,msgId,prompt, model);
    }


    /**
     * 流式聊天post请求
     * @param chatMsgDto
     * @return
     */
    @CrossOrigin
    @PostMapping("sse/post")
    @ReqLimit(permitsPerSecond = 10)
    @ApiOperation(value = "gpt 发起聊天(数据流式返回) POST 方式",httpMethod = "POST")
    public SseEmitter sendMsg(@Validated @RequestBody ChatMsgDto chatMsgDto){
        return chatGptService.chatSee(chatMsgDto.getWinId(),chatMsgDto.getMsgId(),
                chatMsgDto.getPrompt(), chatMsgDto.getModel());
    }

    @PassPath
    @PostMapping("model/{model}")
    @ReqLimit(permitsPerSecond = 30)
    @ApiOperation(value = "查询gpt模型",httpMethod = "POST")
    public AjaxResult selectGpt3(@PathVariable GptKeyTypeEnum model) {

        return AjaxResult.success(chatGptService.findModelList(model));
    }

}
