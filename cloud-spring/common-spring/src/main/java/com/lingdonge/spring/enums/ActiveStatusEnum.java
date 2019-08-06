package com.lingdonge.spring.enums;

import com.lingdonge.core.bean.base.NameValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ActiveStatusEnum {

    UNACTIVE(0, "未激活"),
    ACTIVE(1, "已激活"),
    ;

    private Integer value;
    private String name;

    private ActiveStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static String getName(Integer value) {
        for (ActiveStatusEnum item : ActiveStatusEnum.values()) {
            if (item.value.equals(value)) {
                return item.name;
            }
        }
        return null;
    }

    public static List<NameValue> getItemList() {
        return Arrays.stream(values()).map(item -> new NameValue(item.name, item.value)).collect(Collectors.toList());
    }

}
