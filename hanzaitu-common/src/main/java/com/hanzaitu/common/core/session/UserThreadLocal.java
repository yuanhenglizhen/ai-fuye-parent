package com.hanzaitu.common.core.session;

import java.util.Objects;
import java.util.Set;

public class UserThreadLocal {


    /**
     * 用户信息线程变量
     */
    private static final ThreadLocal<UserSession> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 变更之前的用户变量
     */
    private static final ThreadLocal<UserSession> BEFORE_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取用户信息, 如果是开发模块，userSession为null时，返回系统用户
     */
    public static UserSession get() {
        UserSession userSession = USER_THREAD_LOCAL.get();
        if(Objects.isNull(userSession)){
//            USER_THREAD_LOCAL.set(createSystemUser());
        }
        return USER_THREAD_LOCAL.get();
    }

    public static void set(UserSession userSessionDTO) {
        USER_THREAD_LOCAL.set(userSessionDTO);
    }

    /**
     * 设置新的用户会话并暂存当前用户会话
     */
    public static void setAndStorage(UserSession userSessionDTO){
        if(Objects.nonNull(USER_THREAD_LOCAL.get())){
            BEFORE_USER_THREAD_LOCAL.set(USER_THREAD_LOCAL.get());
        }
        USER_THREAD_LOCAL.set(userSessionDTO);
    }

    /**
     * 恢复到上次的会话
     */
    public static void recover(){
        if(Objects.nonNull(BEFORE_USER_THREAD_LOCAL.get())){
            USER_THREAD_LOCAL.set(BEFORE_USER_THREAD_LOCAL.get());
        }
    }

    /**
     * 移除用户会话
     */
    public static void remove() {
        USER_THREAD_LOCAL.remove();
        BEFORE_USER_THREAD_LOCAL.remove();
    }


}
