package com.hanzaitu.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = AppProperties.APP_PREFIX)
public class AppProperties {


    public final static String APP_PREFIX = "hzt";

    /**
     * 访问BaseUrl
     */
    private String baseUrl = "";

    /**
     * 静态文件目录
     */
    private String staticDir;


    /** 邀请链接 */
    private String inviteLink;

    private Auth auth = new Auth();

    private Excel excel = new Excel();

    private Config config = new Config();

    private Swagger swagger = new Swagger();

    private MQ mq = new MQ();

    private File file = new File();

    private Ftp ftp = new Ftp();

    @Data
    public static class Config{

        private boolean enableThreadPool = true;

        private boolean enableMybatis = true;

        private boolean enableRedis = true;

        private boolean enableXxlJob = false;

        private boolean enableScheduler = false;

        private boolean enableMq = false;

    }

    @Data
    public static class Auth {

        /**
         * 是否启动授权验证
         */
        private boolean enable = false;

        /**
         * 不需要授权就能访问的Url正则表达式
         */
        private String exclusionUrl;

        /**
         * session失效时间,单位分钟
         */
        private Long sessionExpireTime = 120L;

        /**
         * session失效时间,单位分钟,默认一天
         */
        private Long appSessionExpireTime = 24*60L;

        /**
         * 配置访问token标识
         */
        private String accessTokenName = "Access-Token";

        /**
         * 配置Redis访问token标识前缀
         */
        private String accessTokenKeyPrefix = "hzt:token:access_token";

        /**
         * 配置session类
         */
        private String sessionClass ;

    }

    @Data
    public static class Excel {

        /**
         * excel文件保存路径
         */
        private String savePath = "excel";

        /**
         * excel文件访问路径
         */
        private String accessUrl = "/hanzaitu-excel";

//        /**
//         * 模板访问地址, 特殊情况使用
//         */
//        private String templateAccessUrl;

    }

    @Data
    public static class Swagger {

        /**
         * 标题
         */
        private String title = "接口文档";

        /**
         * 公司
         */
        private String contact = "hanzaitu";

        /**
         * 登录地址
         */
        private String securityUrl;

        /**
         * 基础包
         */
        private String basePackage;
    }

    @Data
    public static class Topic{

        /**
         * 主题名称
         */
        private String name;

        /**
         * 消费者数量
         */
        private int consumerNumber = 1;
    }

    @Data
    public static class MQ {

        /**
         * 主题名称，多个主题用,号隔开
         */
        private List<Topic> topics;

        /*        *//**
         * 消费组名称
         *//*
        private String groupName;

        *//**
         * 消费者名称
         *//*
        private String consumerName;*/
    }

    @Data
    public static class File {

        /**
         * 上传文件的存放路径
         */
        private String savePath = "/tmp";

        /**
         * 访问url,如果是本地存储，则为空
         */
        private String accessUrl = "/browse";

    }

    @Data
    public static class Ftp {

        /**
         * 域名或者IP
         */
        private String host;

        /**
         * 端口
         */
        private Integer port = 21;

        /**
         * 用户
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 编码
         */
        private String charset = "UTF-8";

        /**
         * 连接超时时长，单位毫秒
         */
        private long connectionTimeout = 30000;

        /**
         * Socket连接超时时长，单位毫秒
         */
        private long soTimeout = 10*60*1000;

        /**
         * 设置服务器语言 "zh_CN.UTF-8"
         */
        private String serverLanguageCode;

        /**
         * 设置服务器系统关键词
         */
        private String systemKey;

        /**
         * 主动或者被动模式,默认主动模式
         */
        private String mode = "active";

    }
}
