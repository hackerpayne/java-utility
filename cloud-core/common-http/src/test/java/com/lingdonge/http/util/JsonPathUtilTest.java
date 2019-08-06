package com.lingdonge.http.util;

import org.junit.Test;

public class JsonPathUtilTest {

    String json = "{\n" +
            "  \"code\": 0,\n" +
            "  \"msg\": null,\n" +
            "  \"result\": {\n" +
            "    \"userId\": 1550914541181,\n" +
            "    \"mobile\": \"15066429069\",\n" +
            "    \"source\": \"MOP-R360-YQMM\",\n" +
            "    \"channel\": \"mop\",\n" +
            "    \"brand\": null,\n" +
            "    \"merchant\": null,\n" +
            "    \"sid\": null,\n" +
            "    \"aid\": null,\n" +
            "    \"realName\": \"徐永山\",\n" +
            "    \"idcardNo\": \"371502198907286491\"\n" +
            "  }\n" +
            "}";


    @Test
    public void getJsonValueStr() {
        System.out.println("JSon测试");
        System.out.println(JsonPathUtil.getJsonValueStr(json, "$.result.mobile"));
    }

    @Test
    public void getJsonValue() {
        System.out.println("JSon测试Integer");
        System.out.println(JsonPathUtil.getJsonValue(json, "$.code", Integer.class));
    }
}