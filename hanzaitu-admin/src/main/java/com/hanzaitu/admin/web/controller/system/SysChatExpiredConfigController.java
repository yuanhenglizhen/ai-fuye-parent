package com.hanzaitu.admin.web.controller.system;


import com.hanzaitu.admin.system.domain.AiChatExpiredConfig;
import com.hanzaitu.admin.system.service.ISysConfigQnyService;
import com.hanzaitu.common.core.domain.AjaxResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@ApiOperation("聊天记录保留时间")
@RequestMapping("/chat/cxpired")
@RestController
public class SysChatExpiredConfigController {

    @Autowired
    private ISysConfigQnyService sysConfigQnyService;

    @ApiOperation("查询配置详情")
    @PostMapping("info")
    public AjaxResult getChatExpired() {
        return AjaxResult.success(sysConfigQnyService.getAiChatExpiredConfig());
    }

    @ApiOperation("更新聊天记录配置详情")
    @PostMapping("save")
    public AjaxResult saveChatExpired(@Validated @RequestBody AiChatExpiredConfig aiChatExpiredConfig) {
        return AjaxResult.success(sysConfigQnyService.saveAiChatExpiredConfig(aiChatExpiredConfig));
    }

}
