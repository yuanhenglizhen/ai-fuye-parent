package com.hanzaitu.ai.business.controller;

import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.ai.business.param.LoginParam;
import com.hanzaitu.ai.business.service.LoginService;
import com.hanzaitu.ai.business.service.NotifyMsgService;
import com.hanzaitu.ai.draw.service.NotifyService;
import com.hanzaitu.common.core.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "通知消息")
@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Autowired
    private RedisScript<Long> limitScript;

    @Autowired
    private NotifyMsgService notifyMsgService;

    @ApiOperation(value = "用户消息列表", httpMethod = "POST")
    @PostMapping("user")
    public AjaxResult userNotify() {
        return AjaxResult.success(notifyMsgService.selectUserNotifyList());
    }

    @PassPath
    @ApiOperation(value = "弹框通知", httpMethod = "POST")
    @PostMapping("sys")
    public AjaxResult sysNotify() {
        return AjaxResult.success(notifyMsgService.selectSysNotifyList());
    }

    @PassPath
    @ApiOperation(value = "首页列表通知", httpMethod = "POST")
    @PostMapping("home")
    public AjaxResult homeNotify() {
        limitScript.getScriptAsString();
        return AjaxResult.success(notifyMsgService.selectHomeNotifyList());
    }


}
