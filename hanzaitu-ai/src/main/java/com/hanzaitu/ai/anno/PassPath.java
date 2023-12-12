package com.hanzaitu.ai.anno;


import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD,ElementType.TYPE}) //作用于类和方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface PassPath {

}
