package com.hanzaitu.ai.business.controller;


import com.hanzaitu.ai.business.service.ConfigInfoService;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.common.core.domain.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Api(tags = "配置查询")
@RequestMapping("config")
@RestController
public class ConfigInfoController {

    @Resource
    private ConfigInfoService configInfoService;

    @Autowired
    private UploadLoadConfig uploadLoadConfig;

    @ApiOperation("配置信息查询")
    @PostMapping("info")
    public AjaxResult getInfo() {
        return AjaxResult.success(configInfoService.getPayConfigInfo());
    }

    @ApiOperation("首页轮播图查询")
    @PostMapping("banner")
    public AjaxResult getBanner() {
        return configInfoService.getHomeBannerList();
    }


    @GetMapping("/mj-img/{imgName}")
    public void download(@PathVariable("imgName") String name, HttpServletResponse response) throws IOException {
        String pictureUrl = uploadLoadConfig.getQnyImageDomain()+"/" + name;

        //建立图片连接
        URL url = new URL(pictureUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        //设置请求方式
        connection.setRequestMethod("GET");
        //设置超时时间
        connection.setConnectTimeout(10*1000);
        //输入流
        InputStream stream = connection.getInputStream();
        //通过输出流，将文件写回到浏览器，在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();
        //设置响应回去的是什么类型的文件
        //TODO 这里可以通过七牛云官方SDK获取这个文件的类型
        //response.setContentType("image/jpg");
        //i/o读取
        int length = 0;
        byte[] bytes = new byte[1024];
        while (((length = stream.read(bytes)) != -1)) {
            outputStream.write(bytes, 0, length);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        stream.close();
    }

}
