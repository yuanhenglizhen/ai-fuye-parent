package com.hanzaitu.common.core.annotion;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResourcePermission {

    /**
     * 是否为默认权限，即登录后就有的权限
     * 如果设置为true，则访问被这个注解修饰的controller的方法登录后就可以直接访问，不需要配置权限
     * @return
     */
    boolean isDefault() default false;

    /**
     * 关联资源，只要有这个关联资源的权限，
     * 就拥有被这个注解修饰的controller方法的权限
     * @return
     */
    String[] relationResource() default "";
}
