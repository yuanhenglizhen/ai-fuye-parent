package com.hanzaitu.admin.web.controller.system;

import com.hanzaitu.admin.system.domain.dto.AiHomeBannerDto;
import com.hanzaitu.admin.system.service.ISysHomeService;
import com.hanzaitu.common.core.domain.AjaxResult;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("home")
public class SysHomeConfigController {

    @Autowired
    private ISysHomeService sysHomeService;


    /**
     * 添加首页轮播图
     * @param aiHomeBannerDto
     * @return
     */
    @ApiModelProperty("添加banner")
    @PostMapping("banner/save")
    public AjaxResult save(AiHomeBannerDto aiHomeBannerDto) {
        return sysHomeService.save(aiHomeBannerDto);
    }


}
