package com.hanzaitu.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Order(1)
@Component
public class SpringApplicationUtils {


    private static ApplicationContext context;


    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        SpringApplicationUtils.context = context;
    }


    public static void setContext(ApplicationContext context) {
        SpringApplicationUtils.context = context;
    }

    public static ApplicationContext getApplicationContext()  {
        return context;
    }

    public static <T> T getBeanByName(String beanName) {
        return context == null ? null : (T)context.getBean(beanName);
    }

    public static <T> T getBeanByType(Class<T> clazz) {

        return context == null ? null : context.getBean(clazz);
    }

    /**
     * 根据类型获取所有bean，包括子类
     * @param clazz
     * @return
     */
    public static <T> Collection<T> getBeansOfType(Class<T> clazz){
        return context.getBeansOfType(clazz).values();
    }

    /**
     * 获取当前的profile
     *
     * @return
     */
    public static String getActiveProfile() {
        return context.getEnvironment().getActiveProfiles()[0];
    }

    /**
     * 是否为开发环境
     *
     * @return
     */
    public static boolean getDevMode() {
        String env = getActiveProfile();
        return "dev".equalsIgnoreCase(env) || "local".equalsIgnoreCase(env) || "druid".equalsIgnoreCase(env);
    }

    /**
     * 是否为测试环境
     *
     * @return
     */
    public static boolean getTestMode() {
        String env = getActiveProfile();
        return "test".equalsIgnoreCase(env);
    }

    /**
     * 是否为线上环境
     * 当环境不为dev，local，test时，判定为线上环境
     * @return
     */
    public static boolean isOnlineMode() {
        String env = getActiveProfile();
        return !"dev".equalsIgnoreCase(env) && !"local".equalsIgnoreCase(env) && !"test".equalsIgnoreCase(env);
    }

    /**
     * 注册Bean
     * @param beanName
     * @param obj
     */
    public static void registerSingleton(String beanName, Object obj) {
        ((ConfigurableApplicationContext)context).getBeanFactory().registerSingleton(beanName, obj);
    }
}
