package com.hanzaitu.admin.web.controller.system;


import com.hanzaitu.admin.system.domain.AiAppRelease;
import com.hanzaitu.admin.system.domain.AiSysNotify;
import com.hanzaitu.admin.system.domain.AiUserNotify;
import com.hanzaitu.admin.system.service.ISysAiNotifyService;
import com.hanzaitu.admin.web.controller.common.BaseController;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.page.TableDataInfo;
import com.hanzaitu.common.domain.SysConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "通知配置")
@RestController
@RequestMapping("/system/notify")
public class SysAiNotifyController extends BaseController {

    @Autowired
    public ISysAiNotifyService sysAiNotifyService;

    /**
     * 获取系统通知
     */
    @ApiOperation("获取系统通知")
    @PostMapping("/sys-info")
    public AjaxResult sysInfo() {
        return AjaxResult.success(sysAiNotifyService.getSysNotify());
    }

    /**
     * 更新系统通知
     * @param aiSysNotify
     * @return
     */
    @ApiOperation("更新系统通知")
    @PostMapping("/sys-info/save")
    public AjaxResult sysInfoSave(@Validated @RequestBody AiSysNotify aiSysNotify) {
        return sysAiNotifyService.saveSysNotify(aiSysNotify) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 获取用户通知
     */
    @ApiOperation("获取用户通知列表")
    @PostMapping("/user-info")
    public TableDataInfo userInfo() {
        startPage();
        return getDataTable(sysAiNotifyService.getUserNotify());
    }

    /**
     * 更新用户通知
     */
    @ApiOperation("更新用户通知")
    @PostMapping("/user-info/save")
    public AjaxResult userInfoSave(@RequestBody AiUserNotify aiUserNotify) {
        return sysAiNotifyService.saveUserNotify(aiUserNotify) > 0 ? AjaxResult.success()
                : AjaxResult.error();
    }

    /**
     * 删除用户通知
     */
    @ApiOperation("删除用户通知")
    @PostMapping("/user-info/delete/{ids}")
    public AjaxResult userInfodel(@PathVariable Integer ids) {
        return sysAiNotifyService.deleteUserNotify(ids) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

}
