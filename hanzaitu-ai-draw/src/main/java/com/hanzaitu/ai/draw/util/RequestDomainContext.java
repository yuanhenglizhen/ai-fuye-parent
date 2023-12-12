package com.hanzaitu.ai.draw.util;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class RequestDomainContext {

    //用于填充的update_user为Long类型
    private static ThreadLocal<ConcurrentMap<String,String>> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     *
     * @param id
     */
    public static void setDomainInfo(ConcurrentMap<String,String> currentInfo) {
        threadLocal.set(currentInfo);
    }

    /**
     * 获取值
     *
     * @return
     */
    public static ConcurrentMap<String,String> getDomainInfo() {
        return threadLocal.get();
    }

    /**
     * 删除值
     */
    public static void removeDomainInfo() {
        threadLocal.remove();
    }
}


