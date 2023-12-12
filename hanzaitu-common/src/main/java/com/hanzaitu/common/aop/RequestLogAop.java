package com.hanzaitu.common.aop;

import cn.hutool.json.JSONUtil;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.result.OsStatusEnum;
import com.hanzaitu.common.core.result.ResultException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Aspect
@Slf4j
public class RequestLogAop {

    private static final ThreadLocal<Long> TIME_THREAD_LOCAL = new ThreadLocal<>();

    private static final Logger SLOW_REQUEST_LOG = LoggerFactory.getLogger("SLOW_REQUEST_LOG");

    private static Long MILLI_SECONDS = 1000L;

    private List<String> excludeUrls = new ArrayList<>();

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    @Pointcut("execution(public * com.hanzaitu..*.controller..*.*(..))")
    private void controllerAspect() {}

    /**
     * 方法前置处理 有无异常都执行
     */
    @Before("controllerAspect()")
    public void before() {
        TIME_THREAD_LOCAL.set(System.currentTimeMillis());
    }


    /**
     * 方法正常退出处理 无异常退出-执行
     */
    @AfterReturning(returning = "rvt", pointcut = "controllerAspect()")
    public void afterReturning(JoinPoint point, Object rvt) {
        getMonitorData(point, rvt, false);
    }

    /**
     * 方法异常中断处理 出现异常-执行 传播异常行为
     */
    @AfterThrowing(pointcut = "controllerAspect()", throwing = "a")
    public void afterThrowing(JoinPoint point, Throwable a) {
        getMonitorData(point, a, true);
    }

    /**
     * 组装监控数据
     */
    private void getMonitorData(JoinPoint point, Object result, boolean isException) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String requestParam = "";
        String responseBody = "";
        // 判断是否记录请求日志
        if(excludeUrls.contains(request.getServletPath())){
            return;
        }
        // 请求信息
        if (HttpMethod.POST.name().equals(method)) {
            Object[] args = point.getArgs();
            if (args != null && args.length>0) {
//                for (Object arg : args) {
//                    if (!(arg instanceof org.springframework.validation.BeanPropertyBindingResult)
//                            && !(arg instanceof MultipartFile)
//                            && !(arg instanceof HttpServletRequest)
//                            && !(arg instanceof HttpServletResponse)) {
//                        requestParam = requestParam + JSON.toJSONString(arg);
//                    }
//                }
            }
        }
        // get和post都可能把url作为传参
        if(StringUtils.isNotBlank(request.getQueryString())){
            url = url +"?"+ request.getQueryString();
        }

        // 返回信息
        if (isException) {
            if (result instanceof ResultException) {
                ResultException resultException = (ResultException) result;
                responseBody = JSONUtil.formatJsonStr(AjaxResult.error(resultException.getMessage(), resultException.getStatus()).toString());
            } else {
                Throwable throwable = (Throwable) result;
                responseBody = JSONUtil.formatJsonStr(AjaxResult.error(throwable.getMessage(), OsStatusEnum.SYSTEM_ERROR.getStatus()).toString());

            }
            Throwable throwable = (Throwable) result;
            log.error(throwable.getMessage(), throwable);
        } else {
            if (result != null) {
                responseBody = JSONUtil.formatJsonStr(result.toString());
            }
        }
        if(Objects.nonNull(responseBody) && responseBody.length() > 1000){
            responseBody = responseBody.substring(0, 500)+"...";
        }
        // 耗时毫秒
        String elapsedTimeStr = "-1";
        Long startTime = TIME_THREAD_LOCAL.get();
        if (startTime != null) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if(elapsedTime < MILLI_SECONDS){
                elapsedTimeStr = elapsedTime+"ms";
            }else {
                elapsedTimeStr = BigDecimal.valueOf(elapsedTime).divide(new BigDecimal(MILLI_SECONDS), 3, RoundingMode.HALF_UP)+"s";
            }
            // 当请求耗时大于5秒时告警
            if(elapsedTime > 5 * MILLI_SECONDS){
                SLOW_REQUEST_LOG.warn(
                        "[请求日志]-{\"terminalType\":\"{}\",\"url\":\"{}\",\"method\":\"{}\",\"request\":{},\"response\":{},\"elapsedTime\":\"{}\"}",
                        "", url, method, requestParam, responseBody, elapsedTimeStr);
            }
        }
        log.info(
                "[请求日志]-{\"terminalType\":\"{}\",\"url\":\"{}\",\"method\":\"{}\",\"request\":{},\"response\":{},\"elapsedTime\":\"{}\"}",
                "", url, method, requestParam, responseBody, elapsedTimeStr);
        TIME_THREAD_LOCAL.remove();
    }
}
