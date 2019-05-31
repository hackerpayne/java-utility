package com.lingdonge.spring.enums;


import com.lingdonge.core.bean.base.NameValue;

import java.util.ArrayList;
import java.util.List;

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

    public static String getName(Integer value) {
        for (EnvironmentEnum item : values()) {
            if (value != null && item.value.equals(value)) {
                return item.name;
            }
        }
        return "未知类型-" + value;
    }

    public static List<NameValue> getItemList() {
        List<NameValue> dataList = new ArrayList<NameValue>();
        for (EnvironmentEnum item : values()) {
            dataList.add(new NameValue(item.name, item.value));
        }
        return dataList;
    }

}
