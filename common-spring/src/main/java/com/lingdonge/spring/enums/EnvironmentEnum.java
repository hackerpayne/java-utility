package com.lingdonge.spring.enums;


import com.lingdonge.core.bean.base.NameValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 开发环境枚举
 */
public enum EnvironmentEnum {
    LOCAL("local", "本地"),
    DEV("dev", "开发"),
    TEST("test", "测试"),
    STG("stg", "预上线环境"),
    PROD("prod", "生产"),
    CUSTOM("custom", "自定义环境"),
    ;


    private String value;
    private String name;

    EnvironmentEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public static EnvironmentEnum getItem(String value) {
        return Arrays.stream(values()).filter(item -> item.value.equals(value)).findAny().get();
    }

    public static List<NameValue> getItemList() {
        return Arrays.stream(values()).map(item -> new NameValue(item.name, item.value)).collect(Collectors.toList());
    }

}
