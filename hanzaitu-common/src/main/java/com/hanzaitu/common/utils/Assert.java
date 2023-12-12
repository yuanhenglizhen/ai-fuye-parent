package com.hanzaitu.common.utils;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.hanzaitu.common.core.result.IOsStatusEnum;
import com.hanzaitu.common.core.result.OsStatusEnum;
import com.hanzaitu.common.core.result.ResultException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class Assert {


    /**
     * 断言指定的expression是true，
     *
     * @param expression
     * @param message
     */
    public static void isTrue(boolean expression, String message, Object... params) {
        if (!expression) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言指定的expression是true，
     *
     * @param expression
     * @param em
     */
    public static void isTrue(boolean expression, IOsStatusEnum em, Object... params) {
        if (!expression) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言指定的expression是false，
     *
     * @param expression
     * @param message
     */
    public static void isFalse(boolean expression, String message, Object... params) {
        if (expression) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言指定的expression是false，
     *
     * @param expression
     * @param em
     */
    public static void isFalse(boolean expression, IOsStatusEnum em, Object... params) {
        if (expression) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言指定的Object不是null
     *
     * @param object
     * @param message
     */
    public static void notNull(Object object, String message, Object... params) {
        if (object == null) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言指定的Object不是null
     *
     * @param object
     * @param em
     */
    public static void notNull(Object object, IOsStatusEnum em, Object... params) {
        if (object == null) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言指定的字符串不为空，为空则抛出异常
     *
     * @param str
     * @param message
     */
    public static void notEmpty(String str, String message, Object... params) {
        if (StringUtils.isEmpty(str)) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言指定的字符串不为空，为空则抛出异常
     *
     * @param str
     * @param em
     */
    public static void notEmpty(String str, IOsStatusEnum em, Object... params) {
        if (StringUtils.isEmpty(str)) {
            throw ResultException.createResultException(em, params);
        }
    }


    /**
     * 断言指定的字符串不为空白，为空则抛出异常
     *
     * @param str
     * @param message
     */
    public static void notBlank(String str, String message, Object... params) {
        if (StringUtils.isBlank(str)) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言指定的字符串不为空白，为空则抛出异常
     *
     * @param str
     * @param em
     */
    public static void notBlank(String str, IOsStatusEnum em, Object... params) {
        if (StringUtils.isBlank(str)) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言集合不为空，为空则抛出异常
     *
     * @param coll
     * @param message
     */
    public static void notEmpty(Collection<?> coll, String message, Object... params) {
        if (CollectionUtils.isEmpty(coll)) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言集合不为空，为空则抛出异常
     *
     * @param coll
     * @param em
     */
    public static void notEmpty(Collection<?> coll, IOsStatusEnum em, Object... params) {
        if (CollectionUtils.isEmpty(coll)) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言数组不为空，为空则抛出异常
     *
     * @param array
     * @param message
     */
    public static void notEmpty(Object[] array, String message, Object... params) {
        if (ArrayUtils.isEmpty(array)) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言数组不为空，为空则抛出异常
     *
     * @param array
     * @param em
     */
    public static void notEmpty(Object[] array, IOsStatusEnum em, Object... params) {
        if (ArrayUtils.isEmpty(array)) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言integer大于0
     *
     * @param integer
     * @param message
     */
    public static void isGreaterThanZero(Integer integer, String message, Object... params) {
        if (integer == null || integer <= 0) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言integer大于0
     *
     * @param integer
     * @param em
     */
    public static void isGreaterThanZero(Integer integer, IOsStatusEnum em, Object... params) {
        if (integer == null || integer <= 0) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言指定的value是枚举中的值
     * @param value
     * @param clazz
     * @param message
     */
    public static void isEnumValue(String value, Class<? extends IEnum> clazz, String message, Object... params) {
        boolean flag = false;
        for (IEnum ienum : clazz.getEnumConstants()) {
            if (ienum.getValue().equals(value)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言指定的value是枚举中的值
     * @param value
     * @param clazz
     * @param em
     */
    public static void isEnumValue(String value, Class<? extends IEnum> clazz, IOsStatusEnum em, Object... params) {
        boolean flag = false;
        for (IEnum ienum : clazz.getEnumConstants()) {
            if (ienum.getValue().equals(value)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言数据库操作成功
     * @param result
     * @param message
     */
    public static void dbSuccess(int result, String message, Object... params) {
        if (result <= 0) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言数据库操作成功
     * @param result
     */
    public static void dbSuccess(boolean result) {
        if (!result) {
            throw ResultException.createResultException("数据库操作失败");
        }
    }

    /**
     * 断言数据库操作成功
     * @param result
     */
    public static void dbSuccess(int result) {
        if (result <= 0) {
            throw ResultException.createResultException("数据库操作失败");
        }
    }


    /**
     * 断言数据库插入成功
     * @param result
     * @param message
     */
    public static void dbInsertSuccess(int result, String message, Object... params) {
        if (result <= 0) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言数据库插入成功
     * @param result
     * @param em
     */
    public static void dbInsertSuccess(int result, IOsStatusEnum em, Object... params) {
        if (result <= 0) {
            throw ResultException.createResultException(em, params);
        }
    }

    /**
     * 断言数据库插入成功
     * @param result
     * @param param
     */
    public static void dbInsertSuccess(int result, Object param) {
        if (result <= 0) {
            throw ResultException.createResultException(OsStatusEnum.DB_INSERT_ERROR, param);
        }
    }

    /**
     * 断言数据库更新成功
     * @param result
     * @param param
     */
    public static void dbUpdateSuccess(int result, Object param) {
        if (result <= 0) {
            throw ResultException.createResultException(OsStatusEnum.DB_UPDATE_ERROR, param);
        }
    }

    /**
     * 断言数据库更新成功
     * @param result
     * @param message
     */
    public static void dbUpdateSuccess(int result, String message, Object... params) {
        if (result <= 0) {
            throw ResultException.createResultException(message, params);
        }
    }

    /**
     * 断言数据库更新成功
     * @param result
     * @param em
     */
    public static void dbUpdateSuccess(int result, IOsStatusEnum em, Object... params) {
        if (result <= 0) {
            throw ResultException.createResultException(em, params);
        }
    }
}
