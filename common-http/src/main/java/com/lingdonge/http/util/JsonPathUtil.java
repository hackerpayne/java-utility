package com.lingdonge.http.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

/**
 *
 */
public class JsonPathUtil {

    /**
     * @param content
     * @param jsonPath
     * @return
     */
    public static String getJsonValueStr(String content, String jsonPath) {
        return getJsonValue(content, jsonPath, String.class);
    }

    /**
     * @param content
     * @param jsonPath
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T getJsonValue(String content, String jsonPath, Class<T> clz) {
        ReadContext ctx = JsonPath.parse(content);
        return ctx.read(jsonPath, clz);
    }

}
