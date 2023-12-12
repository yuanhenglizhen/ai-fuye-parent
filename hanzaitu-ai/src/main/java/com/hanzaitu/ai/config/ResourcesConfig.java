package com.hanzaitu.ai.config;

import com.hanzaitu.ai.draw.config.UploadLoadConfig;
import com.hanzaitu.ai.draw.domain.entity.AiConfigQnyEntity;
import com.hanzaitu.ai.draw.mapper.AiConfigQnyMapper;
import com.hanzaitu.ai.draw.util.FileUtil;
import com.hanzaitu.common.config.AppProperties;
import com.hanzaitu.common.constant.Constants;
import com.hanzaitu.ai.session.AuthInterceptor;
import com.hanzaitu.common.interceptor.RepeatSubmitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 通用配置
 **/
@Configuration
public class ResourcesConfig implements WebMvcConfigurer
{
    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;



    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private UploadLoadConfig uploadLoadConfig;

    @Resource
    private AiConfigQnyMapper aiConfigQnyMapper;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        /** 绘画图片路径 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + FileUtil.getAbsolutePath(uploadLoadConfig.getTmpDir()) + "/");


        /** 上传图片路径 */
        registry.addResourceHandler(Constants.STATIC_RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + FileUtil.getAbsolutePath(appProperties.getStaticDir()) + "/");

        /** swagger配置 */
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());;
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        AuthInterceptor authInterceptor = new AuthInterceptor(contextPath,appProperties.getAuth().getExclusionUrl(),
                appProperties.getAuth().getSessionExpireTime(), appProperties.getAuth().isEnable(),
                appProperties.getAuth().getAccessTokenName(),
                appProperties.getAuth().getAccessTokenKeyPrefix(),
                appProperties.getAuth().getSessionClass(),
                appProperties.getAuth().getAppSessionExpireTime());
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }



    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 有效期 1800秒
        config.setMaxAge(1800L);
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 返回新的CorsFilter
        return new CorsFilter(source);
    }

    @Bean
    protected UploadLoadConfig uploadLoadConfig() {
        UploadLoadConfig uploadLoadConfig = new UploadLoadConfig();
        AiConfigQnyEntity aiConfigQnyEntity = aiConfigQnyMapper.selectOne(null);
        if(aiConfigQnyEntity != null) {
            uploadLoadConfig.setQnyAccessKey(aiConfigQnyEntity.getQnyAccessKey());
            uploadLoadConfig.setQnyImageDomain(aiConfigQnyEntity.getQnyImageDomain());
            uploadLoadConfig.setQnySecretKey(aiConfigQnyEntity.getQnySecretKey());
            uploadLoadConfig.setQnyBucketName(aiConfigQnyEntity.getQnyBucketName());
        }
        uploadLoadConfig.setTmpDir("mj-draw-tmp");
        return uploadLoadConfig;
    }
}