package com.hanzaitu.ai.draw.controller;


import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.domain.dto.DrawQuadratDto;
import com.hanzaitu.ai.draw.domain.dto.DrawQuadratPraiseDto;
import com.hanzaitu.ai.draw.service.DrawSquareService;
import com.hanzaitu.common.annotation.ReqLimit;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.page.PageParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "绘画广场")
@RestController
@RequestMapping("/draw/square")
public class DrawSquareController {

    @Autowired
    private DrawSquareService drawSquareService;

    @ApiOperation(value = "查询绘画广场列表", httpMethod = "POST")
    @PostMapping("list")
    public AjaxResult getList(@Validated @RequestBody DrawQuadratDto drawQuadratDto) {
        return AjaxResult.success(drawSquareService.selectList(drawQuadratDto));
    }

    @ApiOperation(value = "查询绘画广场收藏列表", httpMethod = "POST")
    @PostMapping("collect")
    public AjaxResult getList(@RequestBody PageParam pageParam) {
        return AjaxResult.success(drawSquareService.collectDrawList(pageParam));
    }

    @ApiOperation(value = "查询绘画广场点赞收藏列表", httpMethod = "POST")
    @PostMapping("collect-square")
    public AjaxResult getCollectSquare(@RequestBody PageParam pageParam) {
        return AjaxResult.success(drawSquareService.aiDrawRraiseList(pageParam));
    }

    @ApiOperation(value = "点赞", httpMethod = "POST")
    @PostMapping("square")
    @ReqLimit(permitsPerSecond = 20)
    public AjaxResult square(@Validated @RequestBody DrawQuadratPraiseDto drawQuadratPraiseDto) {
        return AjaxResult.success(drawSquareService.praise(drawQuadratPraiseDto));
    }

    @ApiOperation(value = "查询标签", httpMethod = "POST")
    @PostMapping("label")
    @ReqLimit(permitsPerSecond = 20)
    public AjaxResult label() {
        return AjaxResult.success(drawSquareService.selectLabel());
    }
}
