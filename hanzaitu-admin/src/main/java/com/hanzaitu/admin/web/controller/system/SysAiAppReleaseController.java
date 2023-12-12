package com.hanzaitu.admin.web.controller.system;


import com.hanzaitu.admin.system.domain.AiAppRelease;
import com.hanzaitu.admin.system.service.ISysAiAppReleaseService;
import com.hanzaitu.admin.web.controller.common.BaseController;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.domain.entity.SysDictData;
import com.hanzaitu.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "app发布")
@RestController
@RequestMapping("/app/release")
public class SysAiAppReleaseController extends BaseController {


    @Autowired
    private ISysAiAppReleaseService isysAiAppReleaseService;

    @ApiOperation("app发布列表")
    @PostMapping("list")
    public TableDataInfo getReleaseList() {
        startPage();
        List<AiAppRelease> list = isysAiAppReleaseService.getReleaseList();
        return getDataTable(list);
    }


    @ApiOperation("app发布添加")
    @PostMapping("save")
    public AjaxResult save(@Validated @RequestBody AiAppRelease aiAppRelease){
        return isysAiAppReleaseService.saveAiAppRelease(aiAppRelease) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @ApiOperation("删除")
    @PostMapping("delete/{ids}")
    public AjaxResult del(@PathVariable Integer ids){
        if (ids == null) {
            return AjaxResult.error();
        }
        return isysAiAppReleaseService.deleteAiAppRelease(ids) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

}
