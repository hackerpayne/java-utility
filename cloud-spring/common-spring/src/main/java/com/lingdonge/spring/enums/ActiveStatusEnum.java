package com.lingdonge.spring.enums;

import com.lingdonge.core.bean.base.NameValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public enum ActiveStatusEnum {

    UNACTIVE(0, "未激活"),
    ACTIVE(1, "已激活"),
    ;

    private Integer value;
    private String name;

    ActiveStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static ActiveStatusEnum getItem(Integer value) {
        return Stream.of(values()).filter(item -> item.value.equals(value)).findAny().orElse(null);
    }

    public static List<NameValue> getItemList() {
        return Stream.of(values()).map(item -> new NameValue(item.name, item.value)).collect(toList());
    }

}
