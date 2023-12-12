package com.hanzaitu.ai.draw.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.support.Task;
import com.hanzaitu.ai.draw.util.FileUtil;
import com.hanzaitu.common.config.ServerConfig;
import com.hanzaitu.common.constant.Constants;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.RandomUtil;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.utils.file.FileUtils;
import com.hanzaitu.common.utils.file.ImageUtils;
import com.hanzaitu.common.utils.uuid.UUID;
import com.hanzaitu.common.utils.file.FileUploadUtils;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


@Slf4j
@Service
public class QiniuService {


    @Autowired
    private UploadLoadConfig uploadLoadConfig;

    // 构造一个带指定Zone对象的配置类,不同的七云牛存储区域调用不同的zone
    //private Configuration cfg = new Configuration(Zone.zone2());
    private Configuration cfg = new Configuration(Zone.autoZone());

    // ...其他参数参考类注释
    UploadManager uploadManager = new UploadManager(cfg);

    /**
     * 简单上传，使用默认策略，只需要设置上传的空间名就可以了
     *
     * @param fileName 文件上传至七牛云空间的名称
     * @return
     */
    public String getUpToken(String fileName) {
        // 密钥配置
        Auth auth = Auth.create(uploadLoadConfig.getQnyAccessKey(), uploadLoadConfig.getQnySecretKey());
        //return auth.uploadToken(bucketname);
        //<bucket>:<key>，表示只允许用户上传指定key的文件。在这种格式下文件默认允许“修改”，已存在同名资源则会被本次覆盖。
        //如果希望只能上传指定key的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1。
        //第三个参数是token的过期时间
        return auth.uploadToken(uploadLoadConfig.getQnyBucketName(), fileName, 3600, new StringMap().put("insertOnly", 0));
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            // 判断是否是合法的文件后缀
            if (!FileUtil.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            // 调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken(fileName));
            // 打印返回的信息
            if (res.isOK() && res.isJson()) {
                String imageDomain = uploadLoadConfig.getQnyImageDomain();
                // 返回这张存储照片的地址
                return imageDomain + JSONObject.parseObject(res.bodyString()).get("key");
            } else {
                log.error("七牛异常:" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            log.error("七牛异常:" + e.getMessage());
            return null;
        }
    }

    /**
     * mj返回的图片上传至7牛云
     * @param imgUrl
     */
    public String imageDownUpload(String rawImgUrl, String fileName)
    {
        if (rawImgUrl != null) {
            String fileDir = FileUtil.getAbsolutePath(uploadLoadConfig.getTmpDir());
            String filePath = FileUtil.downRemoteFile(rawImgUrl,fileDir,fileName,null,null);
            if (filePath != null) {
                try {
                    return upload(filePath,fileName);
                } catch (QiniuException e){
                    log.error(e.getMessage());
                }
            }
        }
        return rawImgUrl;
    }

    /**
     * mj返回的图片上传至7牛云
     */
    public String imageDownNotUpload(Task taskParam, Boolean isReduce){
        String rawImgUrl = taskParam.getImageUrl();
        String fileName = taskParam.getId();
        String reqDomain = taskParam.getReqDomain();
        String proxyHost = taskParam.getProxyHost();
        Integer proxyPort = taskParam.getProxyPort();
        if (rawImgUrl != null) {
            String fileDir = FileUtil.getAbsolutePath(uploadLoadConfig.getTmpDir());
            String filePath = FileUtil.downRemoteFile(rawImgUrl,fileDir, fileName, proxyHost, proxyPort);
            if(StringUtils.isNotEmpty(filePath)) {
                if (isReduce) {
                    log.debug("开始压缩。。。");
                    String densityImgPath = FileUtil.densityImg(filePath,fileDir);
                    log.debug("压缩完毕。。。{}",densityImgPath);
                    if (densityImgPath != null){
                        try {
                            FileUtil.deleteFile(filePath);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            log.error("删除失败");
                        }
                        filePath = densityImgPath;
                    }
                }
                BufferedImage bufferedImage = FileUtil.getImgSize(filePath);
                if (bufferedImage != null) {
                    int srcWidth = bufferedImage.getWidth();      // 源图宽度
                    int srcHeight = bufferedImage.getHeight();
                    taskParam.setImgWidth((double) srcWidth);
                    taskParam.setImgHeight((double) srcHeight);
                }
                String uploadFile = uploadLocalUrl(filePath,fileName,reqDomain,fileDir);
                if (StringUtils.isNotEmpty(uploadFile)){
                    return uploadFile;
                }
            }
            BufferedImage bufferedImage = FileUtil.getWebImgSize(rawImgUrl,proxyHost,proxyPort);
            if (bufferedImage != null) {
                int srcWidth = bufferedImage.getWidth();      // 源图宽度
                int srcHeight = bufferedImage.getHeight();
                taskParam.setImgWidth((double) srcWidth);
                taskParam.setImgHeight((double) srcHeight);
            }
        }
        return rawImgUrl;
    }

    public String uploadLocalUrl(String filePath,String fileName,String reqDomain,String fileDir) {
        if (filePath != null) {
            try {
                if (!StringUtils.isAnyEmpty(uploadLoadConfig.getQnyAccessKey(),
                        uploadLoadConfig.getQnySecretKey(), uploadLoadConfig.getQnyImageDomain(),
                        uploadLoadConfig.getQnyBucketName())) {
                    String httpPath = uploadLoadConfig.getQnyImageDomain();
                    if (!httpPath.endsWith("/") && !httpPath.endsWith("\\")) {
                        httpPath = httpPath + "/";
                    }
                    //String upUrl = httpPath + upload(filePath, fileName);
                    String upUrl = upload(filePath, fileName);
                    FileUtil.deleteFile(filePath);
                    //return "/config/mj-img/"+upUrl;
                    return "/"+upUrl;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            String currentPath = StringUtils.substring(filePath,fileDir.length()+1);
            //return reqDomain +"/api"+ Constants.RESOURCE_PREFIX+"/"+currentPath;
            return Constants.RESOURCE_PREFIX+"/"+currentPath;
        }
        return "";
    }



    /**
     * 普通上传
     *
     * @param filePath 文件路径
     * @throws IOException
     */
    public String upload(String filePath, String fileNameSuffix) throws QiniuException {
        try {
            log.debug("收到路径："+filePath);
            //String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + FilenameUtils.getName(url.getPath());
            //String fileName = FileUtil.getStitchingName(new URL("file://" + filePath),fileNameSuffix);
            String fileName =  FilenameUtils.getName(new URL("file://" + filePath).getPath());

            log.debug("文件名："+fileName);
            log.debug("文件上传中："+fileName);
            //调用put方法上传
            Response res = uploadManager.put(filePath, fileName, getUpToken(fileName));

            log.debug("七牛云返回信息："+res.bodyString());
            JSONObject jsonObject= JacksonUtil.parseObject(res.bodyString(), JSONObject.class);
            //打印返回的信息
            return jsonObject.getString("key");
        }catch (MalformedURLException e){
            throw new RuntimeException(e);
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息

            try {
                //响应的文本信息
                log.error(r.bodyString());
            }catch (QiniuException e1){
                e.printStackTrace();
            }
            throw e;
        }
    }





}
