package com.lingdonge.spring.enums;

import com.lingdonge.core.bean.base.NameValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum YesNoEnum {
    NO(0, "否"),
    YES(1, "是");

    private Integer value;
    private String name;

    YesNoEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static YesNoEnum getItem(Integer value) {
        return Stream.of(values()).filter(item -> item.value.equals(value)).findAny().orElse(null);
    }

    public static List<NameValue> getItemList() {
        return Stream.of(values()).map(item -> new NameValue(item.name, item.value)).collect(Collectors.toList());
    }
}
