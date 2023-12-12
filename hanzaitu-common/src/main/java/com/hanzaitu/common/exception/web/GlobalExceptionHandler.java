package com.hanzaitu.common.exception.web;

import com.hanzaitu.common.config.ServerConfig;
import com.hanzaitu.common.constant.HttpStatus;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.exception.AuthException;
import com.hanzaitu.common.exception.CertificateVerificationException;
import com.hanzaitu.common.exception.DemoModeException;
import com.hanzaitu.common.exception.ServiceException;
import com.hanzaitu.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 
 * @author ruoyi
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    /**
//     * 权限校验异常
//     */
//    @ExceptionHandler(AccessDeniedException.class)
//    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
//    {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
//        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
//    }

    /**
     * 返回异常
     */
    @ExceptionHandler(ResultException.class)
    public AjaxResult resultException(ResultException e,
                                         HttpServletRequest request)
    {
        Integer statusCode = e.getStatus();
        String msgStr = e.getMsg();
        log.error(msgStr);
        return AjaxResult.error(statusCode,msgStr);
    }


    /**
     * 当前软件未授权
     */
    @ExceptionHandler(CertificateVerificationException.class)
    public AjaxResult unlicensedSoftware(CertificateVerificationException e,
                                                          HttpServletRequest request)
    {
        String tips = String.format("请求地址 %s 未授权", ServerConfig.getDomain(request));
        log.error(tips);
        return AjaxResult.error(HttpStatus.UNLICENSED_SOFTWARE,"当前软件未授权，请联系售后进行授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String tips = String.format("请求地址 %s,不支持 %s 请求", requestURI, e.getMethod());
        log.error(tips);
        //return AjaxResult.error(e.getMessage());
        return AjaxResult.error(tips);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request)
    {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        //return AjaxResult.error(e.getMessage());
        return AjaxResult.error("系统异常,请稍后再试！！");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        //return AjaxResult.error(e.getMessage());
        return AjaxResult.error("系统异常,请稍后再试！！");
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 登录系统异常
     */
    @ExceptionHandler(AuthException.class)
    public AjaxResult authException(AuthException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return AjaxResult.noAuth(e.getMessage());
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e)
    {
        return AjaxResult.error("演示模式，不允许操作");
    }
}
