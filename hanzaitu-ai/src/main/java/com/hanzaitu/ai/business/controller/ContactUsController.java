package com.hanzaitu.ai.business.controller;


import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "联系我们")
@RestController
@RequestMapping("/contactUs")
public class ContactUsController {

    @Autowired
    private ISysConfigService iSysConfigService;


    @PassPath
    @ApiOperation(value = "获取联系我们", httpMethod = "GET")
    @GetMapping("getContactUs")
    public AjaxResult getContactUs() {
        return AjaxResult.success(iSysConfigService.getContactUs());
    }
}
