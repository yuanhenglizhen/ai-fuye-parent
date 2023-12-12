package com.hanzaitu.admin.web.controller.system;

import com.hanzaitu.admin.system.domain.AiChatExpiredConfig;
import com.hanzaitu.admin.system.domain.SysQnyConfig;
import com.hanzaitu.admin.system.mapper.AiChatExpiredConfigMapper;
import com.hanzaitu.admin.system.service.ISysConfigQnyService;
import com.hanzaitu.admin.web.controller.common.BaseController;
import com.hanzaitu.common.core.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "七牛云的配置")
@RestController
@RequestMapping("/qny/config")
public class SysConfigQnyController extends BaseController
{
    @Autowired
    private ISysConfigQnyService configQnyService;


    /**
     * 获取参数配置列表
     */
    @ApiModelProperty("获取七牛云参数详情")
    @PostMapping("/info")
    public AjaxResult info()
    {
        SysQnyConfig sysQnyConfig = configQnyService.getSysQnyConfig();

        return AjaxResult.success( sysQnyConfig != null ? sysQnyConfig : new SysQnyConfig());
    }

    @ApiModelProperty("更新七牛云")
    @PostMapping("/update")
    public AjaxResult update(@Validated @RequestBody SysQnyConfig sysConfig)
    {
        return configQnyService.updateSysQnyConfig(sysConfig) > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
