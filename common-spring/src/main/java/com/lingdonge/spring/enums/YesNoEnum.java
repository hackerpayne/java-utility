package com.lingdonge.spring.enums;

import com.lingdonge.core.bean.base.NameValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum YesNoEnum {
    NO(0, "否"),
    YES(1, "是");

    private Integer value;
    private String name;

    private YesNoEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static String getName(Integer value) {
        for (YesNoEnum item : YesNoEnum.values()) {
            if (value != null && item.value.equals(value)) {
                return item.name;
            }
        }
        return "未知类型-" + value;
    }

    public static List<NameValue> getItemList() {
        return Arrays.stream(values()).map(item -> new NameValue(item.name, item.value)).collect(Collectors.toList());
    }
}
