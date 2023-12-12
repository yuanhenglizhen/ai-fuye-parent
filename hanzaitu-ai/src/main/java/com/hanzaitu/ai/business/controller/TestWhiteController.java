package com.hanzaitu.ai.business.controller;

import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testLogin")
public class TestWhiteController {


    @GetMapping("/test1") //不贴注解
    public AjaxResult test1(){
        return AjaxResult.success();
    }

    @PassPath
    @GetMapping("/test2") //贴注解
    public AjaxResult test2(){
        return AjaxResult.success();
    }
}
