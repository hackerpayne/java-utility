package com.lingdonge.spring.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json 工具类，jackson的一层封装
 */
@Slf4j
public class JsonJacksonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    private JsonJacksonUtil() {

    }

    public static ObjectMapper getInstance() {
        return mapper;
    }

    /**
     * 对象转换为JSON字符串
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return toJson(obj);
    }

    /**
     * 对象转换为JSON字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, obj);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("序列化对象【" + obj + "】时出错", e);
        }
    }

    /**
     * JSON字符串转换为实体类
     *
     * @param entityClass
     * @param jsonString
     * @param <T>
     * @return
     */
    public static <T> T toBean(Class<T> entityClass, String jsonString) {
        try {
            return mapper.readValue(jsonString, entityClass);
        } catch (Exception e) {
            throw new RuntimeException("JSON【" + jsonString + "】转对象时出错", e);
        }
    }

    /**
     * json转Map
     *
     * @param jsonStr
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> toMap(String jsonStr) throws Exception {
        if (jsonStr != null && !"".equals(jsonStr)) {
            return mapper.readValue(jsonStr, Map.class);
        } else {
            return null;
        }
    }

    /**
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, T> toMapBean(String jsonStr, Class<T> clazz) throws Exception {
        Map<String, Map<String, Object>> map = mapper.readValue(jsonStr, new TypeReference<Map<String, T>>() {
        });
        Map<String, T> result = new HashMap<String, T>();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), mapToBean(entry.getValue(), clazz));
        }
        return result;
    }

    /**
     * @param jsonArrayStr
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> toList(String jsonArrayStr, Class<T> clazz) throws Exception {
        List<Map<String, Object>> list = mapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {
        });
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> map : list) {
            result.add(mapToBean(map, clazz));
        }
        return result;
    }

    /**
     * Map转换为Bean
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(Map map, Class<T> clazz) {
        return mapper.convertValue(map, clazz);
    }

//    /**
//     * 转换为Callback格式
//     *
//     * @param value
//     * @param callback
//     * @return
//     */
//    public static MappingJacksonValue jsonp(Object value, String callback) {
//        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(value);
//        mappingJacksonValue.setJsonpFunction(callback);
//        return mappingJacksonValue;
//    }

    /**
     * 用于对象通过其他工具已转为JSON的字符形式，这里不需要再加上引号
     *
     * @param obj
     * @param isObject
     */
    public static String getJsonSuccess(String obj, boolean isObject) {
        String jsonString = null;
        if (obj == null) {
            jsonString = "{\"success\":true}";
        } else {
            jsonString = "{\"success\":true,\"data\":" + obj + "}";
        }
        return jsonString;
    }

    /**
     * 获取结果里面的Success字段
     *
     * @param obj
     * @return
     */
    public static String getJsonSuccess(Object obj) {
        return getJsonSuccess(obj, null);
    }

    /**
     * @param obj
     * @param message
     * @return
     */
    public static String getJsonSuccess(Object obj, String message) {
        if (obj == null) {
            return "{\"success\":true,\"message\":\"" + message + "\"}";
        } else {
            try {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("success", true);
                return "{\"success\":true," + toString(obj) + ",\"message\":\"" + message + "\"}";
            } catch (Exception e) {
                throw new RuntimeException("序列化对象【" + obj + "】时出错", e);
            }
        }
    }

    /**
     * 获取error字段
     *
     * @param obj
     * @return
     */
    public static String getJsonError(Object obj) {
        return getJsonError(obj, null);
    }

    /**
     * 获取Error字段
     *
     * @param obj
     * @param message
     * @return
     */
    public static String getJsonError(Object obj, String message) {
        if (obj == null) {
            return "{\"success\":false,\"message\":\"" + message + "\"}";
        } else {
            try {
                obj = parseIfException(obj);
                return "{\"success\":false,\"data\":" + toString(obj) + ",\"message\":\"" + message + "\"}";
            } catch (Exception e) {
                throw new RuntimeException("序列化对象【" + obj + "】时出错", e);
            }
        }
    }

    /**
     * @param obj
     * @return
     */
    public static Object parseIfException(Object obj) {
        if (obj instanceof Exception) {
            return getErrorMessage((Exception) obj, null);
        }
        return obj;
    }

    /**
     * @param e
     * @param defaultMessage
     * @return
     */
    public static String getErrorMessage(Exception e, String defaultMessage) {
        return defaultMessage != null ? defaultMessage : null;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
