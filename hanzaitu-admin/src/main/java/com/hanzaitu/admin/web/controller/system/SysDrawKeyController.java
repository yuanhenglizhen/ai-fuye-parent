package com.hanzaitu.admin.web.controller.system;


import com.hanzaitu.admin.system.domain.DiscordAuthToken;
import com.hanzaitu.admin.system.domain.GptKey;
import com.hanzaitu.admin.system.service.ISysAiDrawKeyService;
import com.hanzaitu.admin.system.service.ISysGptKeyService;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.domain.ChatKeyEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("draw-key")
public class SysDrawKeyController {

    @Autowired
    private ISysAiDrawKeyService sysAiDrawKeyService;

    /**
     * gpt key  list
     * @return
     */
    @ApiOperation(value = "绘画key列表", httpMethod = "POST")
    @PostMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(sysAiDrawKeyService.selectList());
    }

    @ApiOperation(value = "新增或更新绘画key  ", httpMethod = "POST")
    @PostMapping("/save")
    public AjaxResult add(@Validated @RequestBody DiscordAuthToken discordAuthToken) {
        return sysAiDrawKeyService.save(discordAuthToken) > 0 ? AjaxResult.success() : AjaxResult.error() ;
    }

    @ApiOperation(value = "删除绘画key  ", httpMethod = "POST")
    @PostMapping("/del/{ids}")
    public AjaxResult del(@PathVariable Integer ids){
        if (ids == null) {
            return AjaxResult.error("id 不能为空");
        }
        return sysAiDrawKeyService.del(ids) > 0 ? AjaxResult.success() : AjaxResult.error() ;
    }
}
