package com.hanzaitu.ai.session;

import com.alibaba.fastjson2.JSON;
import com.hanzaitu.ai.anno.PassPath;
import com.hanzaitu.common.config.ServerConfig;
import com.hanzaitu.common.core.annotion.ResourcePermission;
import com.hanzaitu.common.core.redis.RedisUtil;
import com.hanzaitu.common.core.result.OsStatusEnum;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserSession;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.exception.AuthException;
import com.hanzaitu.common.utils.TokenUtil;
import com.hanzaitu.common.utils.bean.BeanUtils;
import com.hanzaitu.common.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor{

    /**
     * 请求上下文路径
     */
    private String contextPath;

    /**
     * 需要排除验证的url
     */
    private String exclusionUrl;

    /**
     * session失效时间,单位分钟
     */
    private Long sessionExpireTime;

    /**
     * app session失效时间,单位分钟
     */
    private Long appSessionExpireTime;

    /**
     * 是否启动授权
     */
    private boolean enableAuth;

    /**
     * 配置访问token标识
     */
    private String accessTokenName ;

    /**
     * 配置Redis访问token标识
     */
    private String accessTokenKeyPrefix;

    /**
     * session对象
     */
    private Class sessionClass;

    public AuthInterceptor(String contextPath, String exclusionUrl, Long sessionExpireTime, boolean enableAuth,
                           String  accessTokenName, String accessTokenKeyPrefix, String sessionClass, Long appSessionExpireTime) {
        this.contextPath = contextPath;
        this.exclusionUrl = exclusionUrl;
        this.sessionExpireTime = sessionExpireTime;
        if(sessionExpireTime == null || sessionExpireTime ==0){
            this.sessionExpireTime = 120L;
        }
        this.enableAuth = enableAuth;
        this.accessTokenName = accessTokenName;
        this.accessTokenKeyPrefix = accessTokenKeyPrefix;
        if(StringUtils.isNotBlank(sessionClass)){
            try {
                this.sessionClass = Class.forName(sessionClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("未找到类："+sessionClass);
            }
        }
        this.appSessionExpireTime = appSessionExpireTime;
    }

    private boolean checkExcluded(String servletPath) {
        if (StringUtils.isEmpty(exclusionUrl)) {
            return false;
        }
        Pattern pattern = Pattern.compile(exclusionUrl);
        return pattern.matcher(servletPath).matches();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!enableAuth){
            return true;
        }
        // log.debug("exclusionUrl->{}", appProperties.getAuth().getExclusionUrl());
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "";
        }
        String currentUrl = contextPath.trim() + request.getServletPath();

        // 判断访问的连接是否需要授权
        if (checkExcluded(currentUrl)) {
            return true;
        }

        if (handler instanceof HandlerMethod){
            PassPath methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(PassPath.class);//获取方法上的注解
            if (Objects.nonNull(methodAnnotation)) {
                return true;
            }
            PassPath annotation = ((HandlerMethod) handler).getMethod().getDeclaringClass().getAnnotation(PassPath.class);//获取类上的注解
            if (Objects.nonNull(annotation)) {
                return true;
            }
        }

//        log.info("auth currentUrl->{}", currentUrl);
        // 获取token
        String accessToken = TokenUtil.getAccessToken(request, accessTokenName);
        if (StringUtils.isBlank(accessToken)) {
            log.error("token not auth->{}", accessToken);
            throw AuthException.createAuthException(OsStatusEnum.UNAUTHORIZED.getStatusDesc());
        }
        // 判断缓存中是否存在user信息
        String accessTokenKey = TokenUtil.getAccessTokenKey(accessToken, accessTokenKeyPrefix);
        Object dto = RedisUtil.get(accessTokenKey);
        if (dto == null) {
            throw AuthException.createAuthException(OsStatusEnum.UNAUTHORIZED.getStatusDesc());
        }
        if(dto instanceof String){
            dto = JSON.parseObject(dto.toString(), sessionClass);
        }
        UserSession userSessionDTO = (UserSession)dto;
        String urlStr = SpringUtils.getBean(ServerConfig.class).getUrl();
        String domainStr = ServerConfig.getDomain(request);
        /*String remortIP = ServerConfig.getRemortIP(request);
        String remoteHost = ServerConfig.getRemoteHost(request);
        Integer remotePort = ServerConfig.getRemotePort(request);*/
        String originHost = request.getHeader("Hzt-Origin");


        String oriHost = request.getHeader("Host");

        log.debug("收到url:{}",urlStr);
        log.debug("收到domainStr:{}",domainStr);
        log.debug("收到oriHost:{}",oriHost);
        /*log.debug("remortIP:{}",remortIP);
        log.debug("remoteHost:{}",remoteHost);
        log.debug("remotePort:{}",remotePort);*/
        log.debug("收到originHost:{}",originHost);

        userSessionDTO.setOperationDomain(StringUtils.isNotEmpty(originHost) ? originHost : oriHost);
        // 设置用户线程变量
        UserThreadLocal.set(userSessionDTO);
        RedisUtil.expire(accessTokenKey, sessionExpireTime, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        // log.debug("user thread local remove");
        UserThreadLocal.remove();
    }

}
