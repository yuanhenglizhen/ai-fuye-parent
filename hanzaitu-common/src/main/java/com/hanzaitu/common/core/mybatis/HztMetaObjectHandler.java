package com.hanzaitu.common.core.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hanzaitu.common.core.session.UserSession;
import com.hanzaitu.common.core.session.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class HztMetaObjectHandler implements MetaObjectHandler {


    private static String CREATE_USER = "createBy";
    private static String CREATE_TIME = "createTime";
    private static String UPDATE_USER = "updateBy";
    private static String UPDATE_TIME = "updateTime";
    private static final ThreadLocal<Boolean> forceCurrentTime = new ThreadLocal<>();

    /**
     * 设置强制更新时间为当前时间
     * @param isForceCurrentTime
     */
    public static void setForceCurrentTime(Boolean isForceCurrentTime){
        forceCurrentTime.set(isForceCurrentTime);
    }

    public static void removeForceCurrentTime(){
        forceCurrentTime.remove();
    }

    /**
     * 填充创建人和更新人，如果创建人或者更新人有值，则不强制替换
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        // 强制重置新增时间和更新时间
        if(Objects.isNull(forceCurrentTime.get()) || forceCurrentTime.get()){
            metaObject.setValue(CREATE_TIME, null);
            if(metaObject.hasSetter(UPDATE_TIME)){
                metaObject.setValue(UPDATE_TIME, null);
            }
        }
        this.strictInsertFill(metaObject, CREATE_TIME, () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictInsertFill(metaObject, UPDATE_TIME, () -> LocalDateTime.now(), LocalDateTime.class);
        UserSession userSession = UserThreadLocal.get();
        if(Objects.nonNull(userSession)){
            this.strictInsertFill(metaObject, CREATE_USER, () -> Optional.ofNullable(userSession.getUserName()).orElse(userSession.getUserName()), String.class);
            this.strictInsertFill(metaObject, UPDATE_USER, () -> Optional.ofNullable(userSession.getUserName()).orElse(userSession.getUserName()), String.class);
        }else{
            this.strictInsertFill(metaObject, CREATE_USER, () -> UserSession.SYSTEM_REAL_NAME, String.class);
            this.strictInsertFill(metaObject, UPDATE_USER, () -> UserSession.SYSTEM_REAL_NAME, String.class);
        }
    }

    /**
     * 修改更新人，如果更新人有值，则不强制替换
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        // 强制重置更新时间
        if(Objects.isNull(forceCurrentTime.get()) || forceCurrentTime.get()){
            if(metaObject.hasSetter(UPDATE_TIME)){
                metaObject.setValue(UPDATE_TIME, null);
            }
        }
        this.strictUpdateFill(metaObject, UPDATE_TIME, () -> LocalDateTime.now(), LocalDateTime.class);
        UserSession userSession = UserThreadLocal.get();
        if(Objects.nonNull(userSession)){
            this.strictUpdateFill(metaObject, UPDATE_USER, () -> Optional.ofNullable(userSession.getUserName()).orElse(userSession.getUserName()), String.class);
        }else{
            this.strictUpdateFill(metaObject, UPDATE_USER, () -> UserSession.SYSTEM_REAL_NAME, String.class);
        }
    }
}
