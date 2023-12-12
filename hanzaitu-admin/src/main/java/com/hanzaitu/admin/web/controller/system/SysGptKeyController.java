package com.hanzaitu.admin.web.controller.system;


import com.hanzaitu.admin.system.domain.GptKey;
import com.hanzaitu.admin.system.service.ISysGptKeyService;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.domain.ChatKeyEntity;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.EnumUtils;

@RestController
@RequestMapping("gpt-key")
public class SysGptKeyController {


    @Autowired
    private ISysGptKeyService sysGptKeyService;

    /**
     * 添加gpt key
     * @param gptKey
     * @return
     */
    @ApiOperation(value = "添加gpt key  ", httpMethod = "POST")
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody GptKey gptKey) {
        int insertCount = sysGptKeyService.insertGptKey(gptKey);
        if (insertCount > 0) {
            ChatKeyEntity chatKeyEntity = new ChatKeyEntity();
            chatKeyEntity.setId(gptKey.getId());
            chatKeyEntity.setChatKey(gptKey.getChatKey());
            chatKeyEntity.setModel(gptKey.getModel());
            chatKeyEntity.setProxyPort(gptKey.getProxyPort());
            chatKeyEntity.setProxyHost(gptKey.getProxyHost());
            chatKeyEntity.setTemperature(gptKey.getTemperature());
            chatKeyEntity.setMaxToken(gptKey.getMaxToken());
            sysGptKeyService.pushRedisAsync(chatKeyEntity);
            return AjaxResult.success();
        }
        return AjaxResult.error();
    }

    @ApiOperation(value = "获取 gpt支持的模型", httpMethod = "POST")
    @PostMapping("/get-model")
    public AjaxResult getGptKey() {

        return AjaxResult.success(EnumUtils.getEnumList(ChatCompletion.Model.class));
    }

    /**
     * gpt key  list
     * @param gptKey
     * @return
     */
    @ApiOperation(value = "获取gpt key 列表  ", httpMethod = "POST")
    @PostMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(sysGptKeyService.selectList());
    }

    @ApiOperation(value = "更新gpt key  ", httpMethod = "POST")
    @PostMapping("/update")
    public AjaxResult update(@Validated @RequestBody GptKey gptKey) {
        return AjaxResult.success(sysGptKeyService.updateGptKey(gptKey));
    }

    @ApiOperation(value = "删除gpt key  ", httpMethod = "POST")
    @PostMapping("/delete/{ids}")
    public AjaxResult update(@PathVariable Integer ids) {
        if (ids == null) {
            return AjaxResult.error("id 不能为空");
        }
        return AjaxResult.success(sysGptKeyService.deleteGptKey(ids));
    }
}
