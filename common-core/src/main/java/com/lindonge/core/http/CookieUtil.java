package com.lindonge.core.http;

import com.lindonge.core.util.StringUtils;

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

    public static void main(String[] args) {

        String cookieStr = "cy=2; cye=beijing; _lxsdk_cuid=16241c7f4a5c6-04567ec097bd07-49526a-13c680-16241c7f4a63e; _lxsdk=16241c7f4a5c6-04567ec097bd07-49526a-13c680-16241c7f4a63e\n" +
                "; _hc.v=54c6d1a3-81e8-c460-3df3-3bdf6acdb8da.1521522048; s_ViewType=10; _tr.u=K8MPYZ2ibC2Ub8sP; dper\n" +
                "=6c9feed4a6fb9d1cea4aec37076f9f1304bf79e56eaf39c1fa65d2524f7e30ee; ua=%E5%B0%8F%E4%BA%94Kyle; ctu=37bb3c00ac45965a81f7a67322ec1193db0eb919d166011b57660cbefdefd6ec\n" +
                "; ll=7fd06e815b796be3df069dec7836c3df; _lxsdk_s=162ae13e26d-2d2-84f-47c%7C%7C17";

        CookieUtil.printCookieStr(cookieStr);
    }
}
