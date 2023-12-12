package com.hanzaitu.ai.exception;

//import com.hanzaitu.chat.domain.Result;

import com.hanzaitu.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author plexpt
 */
@Slf4j
//@RestControllerAdvice
public class GlobalChatExceptionHandler {

    private static final String ERROR_MSG = "服务器挤爆了，请充值或稍后尝试";

    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleBaseException(Exception ex, HttpServletRequest request) {
        log.warn("Handle exception, message={}, requestUrl={}", ex.getMessage(),
                request.getRequestURI());

        //return AjaxResult.error(ex.getMessage());
        return AjaxResult.error(ERROR_MSG);
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult handleDefaultErrorView(Exception ex, HttpServletRequest request) {
        log.error("Handle exception, message={}, requestUrl={}", ex.getMessage(),
                request.getRequestURI(), ex);

        //return AjaxResult.error(ex.getMessage());
        return AjaxResult.error(ERROR_MSG);
    }

}