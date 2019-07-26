package com.lingdonge.spring.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.*;

/**
 * Json 工具类，jackson的一层封装
 */
@Slf4j
public class JacksonUtil {

    private static ObjectMapper mapper = getObjectMapper();

    private JacksonUtil() {

    }

    public static ObjectMapper getInstance() {
        return mapper;
    }

    /**
     * 获取一个ObjectMapper对象，处理了时间和Long型精度失效的问题
     *
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        // 1、解决查询缓存转换异常的问题
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // 2、序列换成json时,将所有的long变成string,因为js中得数字类型不能包含所有的java long值
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        // 3、解决jackson2无法反序列化LocalDateTime的问题
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //不再做统一处理 Springcloud2 fegin 会报日期格式化错误 格式化的日期字段直接加@JsonFormat 注解处理
        //序列化日期格式
        //javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        //javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        //javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());

        //反序列化日期格式
        //javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        //javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        //javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        objectMapper.registerModule(javaTimeModule);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 默认时区设置为上海

        return objectMapper;
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
