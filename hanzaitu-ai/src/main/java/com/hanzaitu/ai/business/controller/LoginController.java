package com.hanzaitu.ai.business.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.ai.business.domain.VerifiyImgCode;
import com.hanzaitu.ai.business.param.*;
import com.hanzaitu.ai.business.service.LoginService;
import com.hanzaitu.ai.util.VerifyImageUtil;
import com.hanzaitu.common.annotation.ReqLimit;
import com.hanzaitu.common.core.controller.BaseController;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.utils.JacksonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.ObjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "登录")
@RestController
@RequestMapping("/login")
public class LoginController extends BaseController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static final String IMG_PATH = "/config/picture/*.*";

    private static final String TEMP_IMG_PATH = "/config/temp/temp.png";

    private static final Long IMG_CACHE_EX_TIME = 180L;

    /**
     * 获取图片，由于spring boot打包成jar之后，获取到获取不到resources里头的图片，对此进行处理
     * @param path
     * @return
     * @author pangxianhe
     * @date 2020年1月2日
     */
    private List<InputStream> queryFileList(String path) {

        //获取容器资源解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<InputStream> filelist = new ArrayList<InputStream>();
        // 获取远程服务器IP和端口
        try {

            //获取所有匹配的文件
            Resource[] resources = resolver.getResources(path);

            for(Resource resource : resources) {
                //获得文件流，因为在jar文件中，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
                InputStream stream = resource.getInputStream();
                String targetFilePath =resource.getFilename();
                File ttfFile = new File(targetFilePath);
               /* if(!ttfFile.getParentFile().exists()) {
                	ttfFile.getParentFile().mkdir();
                }*/
//                FileUtils.copyInputStreamToFile(stream, ttfFile);
                filelist.add(stream);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return filelist;
    }

    @PassPath
    @ApiOperation(value = "登陆", httpMethod = "POST")
    @PostMapping("signin")
    public AjaxResult login(@Validated @RequestBody LoginParam param) {
        if(!verifyImageVerifyCode(param)) {
            return AjaxResult.error("图形验证码，校验失败");
        }
        redisTemplate.delete(String.valueOf(param.getChenckMoveid()));
        return AjaxResult.success(loginService.login(param));
    }

    @PassPath
    @ApiOperation(value = "手机号登陆", httpMethod = "POST")
    @PostMapping("signinPhoneNumber")
    public AjaxResult loginPhoneNumber(@Validated @RequestBody LoginPhoneParam param) {
        if(!verifyImageVerifyCode(param)) {
            return AjaxResult.error("图形验证码，校验失败");
        }
        redisTemplate.delete(String.valueOf(param.getChenckMoveid()));

        return AjaxResult.success(loginService.loginPhoneNumber(param));
    }

    @ApiOperation(value = "登出", httpMethod = "POST")
    @PostMapping("signout")
    public AjaxResult logout() {
        String accessToken = UserThreadLocal.get().getAccessToken();
        loginService.logout(accessToken);
        return AjaxResult.success();
    }

    @PassPath
    @ApiOperation(value = "注册", httpMethod = "POST")
    @PostMapping("register")
    public AjaxResult register(@Validated @RequestBody CustomerUserSaveParam param) {

        if(!verifyImageVerifyCode(param)) {
            return AjaxResult.error("图形验证码，校验失败");
        }
        redisTemplate.delete(String.valueOf(param.getChenckMoveid()));
        return loginService.register(param) ? AjaxResult.success() : AjaxResult.error();
    }

    @PassPath
    @ApiOperation(value = "邮箱注册", httpMethod = "POST")
    @PostMapping("registerMail")
    @ReqLimit(permitsPerSecond = 20,timeout = 300)
    public AjaxResult registerMail(@Validated @RequestBody CustomerUserSaveMailParam param) {

        if(!verifyImageVerifyCode(param)) {
            return AjaxResult.error("图形验证码，校验失败");
        }
        redisTemplate.delete(String.valueOf(param.getChenckMoveid()));

        return loginService.registerEmail(param) ? AjaxResult.success() : AjaxResult.error();
    }

    @PassPath
    @ApiOperation(value = "发送验证码", httpMethod = "POST")
    @PostMapping("sendRegisterCode")
    @ReqLimit(permitsPerSecond = 15,timeout = 300)
    public AjaxResult sendRegisterCode(@Validated @RequestBody BaseParam param) {
        if (!verifyImageVerifyCode(param)){
            return AjaxResult.error("图形验证码，校验失败");
        }
        //redisTemplate.delete(String.valueOf(param.getChenckMoveid()));
        loginService.sendRegisterCode(param.getParam());
        return AjaxResult.success();
    }

    @ApiOperation(value = "发送验证码", httpMethod = "POST")
    @PostMapping("sendResetRegisterCode")
    @ReqLimit(permitsPerSecond = 15,timeout = 300)
    public AjaxResult sendRestRegisterCode(@Validated @RequestBody BaseResetParam param) {
        loginService.sendRegisterCode(param.getParam());
        return AjaxResult.success();
    }

    @PassPath
    @ApiOperation(value = "发送邮箱验证码", httpMethod = "POST")
    @PostMapping("sendMailRegisterCode")
    @ReqLimit(permitsPerSecond = 15,timeout = 300)
    public AjaxResult sendMailRegisterCode(@Validated @RequestBody BaseParam param) {
        if(!verifyImageVerifyCode(param)) {
            return AjaxResult.error("图形验证码，校验失败");
        }
        //redisTemplate.delete(String.valueOf(param.getChenckMoveid()));

        return loginService.sendMailRegisterCode(param.getParam());
    }

    @PassPath
    @ApiOperation(value = "重置密码", httpMethod = "POST")
    @PostMapping("resetPassword")
    @ReqLimit(permitsPerSecond = 20,timeout = 300)
    public AjaxResult resetPassword(@Validated @RequestBody CustomerUserSaveParam param) {
        if(!verifyImageVerifyCode(param)) {
            return AjaxResult.error("图形验证码，校验失败");
        }
        redisTemplate.delete(String.valueOf(param.getChenckMoveid()));
        loginService.resetPassword(param);
        return AjaxResult.success();
    }

    @PassPath
    @ApiOperation(value = "邮箱重置密码", httpMethod = "POST")
    @PostMapping("resetPasswordEmail")
    @ReqLimit(permitsPerSecond = 20,timeout = 300)
    public AjaxResult resetPasswordEmail(@Validated @RequestBody CustomerUserSaveMailParam param) {
        if(!verifyImageVerifyCode(param)) {
            return AjaxResult.error("图形验证码，校验失败");
        }
        redisTemplate.delete(String.valueOf(param.getChenckMoveid()));
        loginService.resetPasswordEmail(param);
        return AjaxResult.success();
    }

    /**
     * 获取验证码
     * @return
     * @throws Exception
     * @author pangxianhe
     * @date 2020年1月2日
     */
    @PassPath
    @RequestMapping(value = "/getImageVerifyCode", method = RequestMethod.POST)
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @ReqLimit(permitsPerSecond = 5,timeout = 300)
    public AjaxResult getImageVerifyCode() throws Exception {

        // 读取图库目录
        List<InputStream> imgList = queryFileList(IMG_PATH);
        int randNum = new Random().nextInt(imgList.size());
        InputStream targetFile = imgList.get(randNum);

        List<InputStream> tempimgList = queryFileList(TEMP_IMG_PATH);
        InputStream tempImgFile = tempimgList.get(0);
        // 根据模板裁剪图片
        Map<String, Object> resultMap = VerifyImageUtil.pictureTemplatesCut(tempImgFile, targetFile);
        int xWidth = (int) resultMap.get("xWidth");
        // sessionId 为key，value滑动距离X轴，缓存120秒

        String chenckMoveid = IdWorker.get32UUID();
        BoundValueOperations<Object, Object> redisOper = redisTemplate.boundValueOps(chenckMoveid);

        redisOper.set(xWidth, IMG_CACHE_EX_TIME, TimeUnit.SECONDS);
        // 移除map的滑动距离，不返回给前端
        resultMap.remove("xWidth");
        resultMap.put("chenckMoveid", chenckMoveid);

        return AjaxResult.success().put("data", JacksonUtil.toJsonString(resultMap));
    }


    /**
     * 验证验证码
     * @return
     * @throws Exception
     * @author pangxianhe
     * @date 2020年1月2日
     */
    @PassPath
    @RequestMapping(value = "/verifyImageVerifyCode", method = RequestMethod.POST)
    @ApiOperation(value = "验证图形验证码", notes = "验证图形验证码")
    public AjaxResult getVerifyImageVerifyCode(@Validated @RequestBody VerifiyImgCode verifiyImgCode) {

        return verifyImageVerifyCode(verifiyImgCode) ? AjaxResult.success() : AjaxResult.error("图形验证码，校验失败");
    }

    /**
     * 验证图形验证码
     * @param param
     * @return
     */
    private boolean verifyImageVerifyCode(VerifiyImgCode param) {
        try {
            // 校验滑块随机数
            if (ObjectUtil.isNull(param.getRemoving()) || StringUtils.isEmpty(param.getChenckMoveid())) {
                return false;
            } else {
                // 获取readis缓存的随机数
                Object x_index_orld = redisTemplate.opsForValue().get(String.valueOf(param.getChenckMoveid()));
                if (ObjectUtil.isNull(x_index_orld)) {
                    return false;
                } else {
                    Double dMoveLength = Double.valueOf(x_index_orld.toString());
                    Double xWidth = Double.valueOf(param.getRemoving());

                    if (Math.abs(xWidth - dMoveLength) > 10) {
                        redisTemplate.delete(String.valueOf(param.getChenckMoveid()));
                        return false;
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

}
