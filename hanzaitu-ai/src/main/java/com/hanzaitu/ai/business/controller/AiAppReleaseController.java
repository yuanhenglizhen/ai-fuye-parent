package com.hanzaitu.ai.business.controller;


import com.hanzaitu.ai.business.dto.GetVersionDto;
import com.hanzaitu.ai.business.service.AiAppReleaseService;
import com.hanzaitu.common.core.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "app版本查询")
@RequestMapping("/app/release")
@RestController
public class AiAppReleaseController {

    @Autowired
    private AiAppReleaseService aiAppReleaseService;

    @ApiOperation(value = "查询版本列表", httpMethod = "POST")
    @PostMapping("/list")
    public AjaxResult getRelease(@Validated @RequestBody GetVersionDto getVersionDto) {
        return AjaxResult.success(aiAppReleaseService.getVersionList(getVersionDto));
    }

}
