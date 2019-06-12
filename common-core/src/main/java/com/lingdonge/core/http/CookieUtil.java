package com.lingdonge.core.http;

import com.lingdonge.core.util.StringUtils;

import javax.servlet.http.Cookie;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cookie处理和操作类
 */
public class CookieUtil {

    /**
     * 解析Cookie头
     *
     * @param cookieHeader
     * @return
     */
    public static Map<String, Cookie> parseCookies(String cookieHeader) {
        Map<String, Cookie> result = new LinkedHashMap<String, Cookie>();
        if (cookieHeader != null) {
            String[] cookiesRaw = cookieHeader.split("; ");
            for (int i = 0; i < cookiesRaw.length; i++) {
                String[] parts = cookiesRaw[i].split("=", 2);
                String value = parts.length > 1 ? parts[1] : "";
                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                result.put(parts[0], new Cookie(parts[0], value));
            }
        }
        return result;
    }

    /**
     * 打印一下Cookie到一个列表里面
     *
     * @param cookieStr
     */
    public static void printCookieStr(String cookieStr) {

        Map<String, Cookie> mapCookies = parseCookies(cookieStr);

        mapCookies.forEach((key, value) -> {
            System.out.println(StringUtils.format(".addCookie(\"{}\",\"{}\");", key, value.getValue()));
        });
    }

}
