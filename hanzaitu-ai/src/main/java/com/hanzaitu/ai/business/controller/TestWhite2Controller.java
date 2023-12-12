package com.hanzaitu.ai.business.controller;

import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PassPath
@RestController
@RequestMapping("/testLogin2")
public class TestWhite2Controller {


    @GetMapping("/test1")
    public AjaxResult test1(){
        return AjaxResult.success();
    }

}
