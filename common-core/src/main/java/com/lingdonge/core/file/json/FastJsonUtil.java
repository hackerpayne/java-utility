package com.lingdonge.core.file.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * FastJson常用类封装
 */
@Slf4j
public class FastJsonUtil {

    public FastJsonUtil() {
    }

    /**
     * @param obj
     * @return
     */
    public static Map beanToMap(Object obj) {
        return beanToMap(obj, true);
    }

    /**
     * Bean转换为Map，忽略里面的Null转为空字符串
     * 可以解决：
     * 1、fastjson生成json时Null属性不显示
     * 2、ES里面比较常用的缺失字段不进索引的问题
     *
     * @param obj
     * @param nullToEmpty 是否把Null处理为Map
     * @return
     */
    public static Map beanToMap(Object obj, Boolean nullToEmpty) {
        if (nullToEmpty) {
            return JSON.parseObject(JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty), Map.class);
        }
        return JSON.parseObject(JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue), Map.class);
    }

    /**
     * 从JSON中取出某个一级Key对应的Value
     *
     * @param jsonStr
     * @param jsonKey
     * @return
     */
    public static String getJsonValue(String jsonStr, String jsonKey) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        return jsonObject.getString(jsonKey);
    }

    /**
     * @param jsonStr
     * @param jsonKey
     * @return
     */
    public static JSONObject getJsonObj(String jsonStr, String jsonKey) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        return jsonObject.getJSONObject(jsonKey);
    }

    /**
     * JSON转Map格式
     *
     * @param json
     * @return
     */
    public static Map<String, String> jsonToMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, String>>() {
        });
    }

    /**
     * JSON转换为List格式的Map数据
     *
     * @param json
     * @return
     */
    public static List<Map<String, Object>> jsonToListMap(String json) {
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    /**
     * 转对象数组
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 转Json对象
     * @param json
     * @return
     */
    public static JSONArray jsonToArray(String json) {
        return JSON.parseArray(json);
    }

    /**
     * Object转换为Json的String字符串
     *
     * @param object
     * @return
     */
    public static String objectToJson(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 转换为指定的Object，未测试
     *
     * @param json
     * @return
     */
    public static Object jsonToObject(String json) {
        return JSON.parseObject(json);
    }

    /**
     * 转指定对象
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> valueType) {
        return JSON.parseObject(json, valueType);
    }

    /**
     * JSON转换为实体类
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToEntity(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }


    /**
     * 转换JSON
     *
     * @param object
     * @return
     */
    public static String toJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 非注解深层次过滤Json里面的某些元素
     *
     * @param o           对象
     * @param excludeKeys 要过滤的字段名称
     * @return
     */
    public static JSON toJson(Object o, String... excludeKeys) {
        List<String> excludes = Arrays.asList(excludeKeys);
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().addAll(excludes);    //重点！！！
        return JSON.parseObject(JSON.toJSONString(o, filter));
    }
}
