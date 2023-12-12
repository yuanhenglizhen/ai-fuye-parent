package com.hanzaitu.common.aop;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.hanzaitu.common.annotation.ReqLimit;
import com.hanzaitu.common.exception.ServiceException;
import com.hanzaitu.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Limit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class ReqLimitAop {

    /**
     * 不同的接口，不同的流量控制
     * map的key为 Limiter.key
     */
    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();

    @Around("@annotation(com.hanzaitu.common.annotation.ReqLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //拿limit的注解
        ReqLimit limit = method.getAnnotation(ReqLimit.class);
        if (limit != null) {
            //key作用：不同的接口，不同的流量控制
            String key=limit.key();
            // 如果没有设置接口限流名称、则默认取方法名称为key
            if (StringUtils.isBlank(key)){
                key = joinPoint.getSignature().getName();
            }

            RateLimiter rateLimiter = null;
            //验证缓存是否有命中key
            if (!limitMap.containsKey(key)) {
                // 创建令牌桶
                rateLimiter = RateLimiter.create(limit.permitsPerSecond());
                limitMap.put(key, rateLimiter);
                log.info("新建了令牌桶={}，容量={}",key,limit.permitsPerSecond());
            }
            rateLimiter = limitMap.get(key);

            // 拿令牌
            boolean acquire = rateLimiter.tryAcquire(limit.timeout(), limit.timeunit());
            // 拿不到命令，直接返回异常提示
            if (!acquire) {
                throw new ServiceException(limit.msg());
            }
        }
        return joinPoint.proceed();
    }
}
