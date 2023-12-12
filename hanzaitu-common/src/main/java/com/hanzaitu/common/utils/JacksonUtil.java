package com.hanzaitu.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 杰克逊跑龙套
 * 1.序列化类上，使用 @JsonInclude(JsonInclude.Include.NON_NULL) 序列化忽略所有null字段
 * 2.时间字段上，使用 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 保持格式统一
 * 3.序列化类上，必须提供无参构造方法！！
 * 4.待续......
 *
 * @author xudong.shen
 * @date 2022/05/06
 */
public class JacksonUtil {


    /**
     * 序列化为JSON字符串
     *
     * @param obj obj
     * @return {@link String}
     */
    public static String toJsonString(Object obj) {
        if (obj == null) return null;
        ObjectMapper mapper = SpringUtil.getBean(ObjectMapper.class);
        String result = null;
        try {
            result = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 解析对象
     * 反序列化为Object
     *
     * @param clazz   clazz
     * @param jsonStr json str
     * @return {@link T}
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        if (StringUtils.isBlank(jsonStr) || clazz == null) return null;
        ObjectMapper mapper = SpringUtil.getBean(ObjectMapper.class);
        T t = null;
        try {
            t = mapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }


    /**
     * 解析List
     * 反序列化为List集合
     *
     * @param clazz       clazz
     * @param listJsonStr 列表json str
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> parseList(String listJsonStr, Class<T> clazz) {
        if (StringUtils.isBlank(listJsonStr) || clazz == null) return Collections.emptyList();
        ObjectMapper mapper = SpringUtil.getBean(ObjectMapper.class);
        JavaType t  = mapper.getTypeFactory().constructParametricType(List.class, clazz);

        List list = Collections.emptyList();
        try {
            list = mapper.readValue(listJsonStr, t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 解析Map
     * 反序列化为Map集合
     * @param mapJsonStr 地图json str
     * @param kClazz     k clazz
     * @param vClazz     v clazz
     * @return {@link Map}<{@link K}, {@link V}>
     */
    public static <K,V> Map<K,V> parseMap(String mapJsonStr, Class<K> kClazz, Class<V> vClazz) {
        if (StringUtils.isBlank(mapJsonStr) || kClazz == null || vClazz == null) return Collections.emptyMap();
        ObjectMapper mapper = SpringUtil.getBean(ObjectMapper.class);
        Map map = Collections.EMPTY_MAP;
        try {
            map = mapper.readValue(mapJsonStr, mapper.getTypeFactory().constructParametricType(Map.class, kClazz, vClazz));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }


}