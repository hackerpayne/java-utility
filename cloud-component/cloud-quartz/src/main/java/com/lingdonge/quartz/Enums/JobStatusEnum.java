package com.lingdonge.quartz.enums;

import com.lingdonge.core.bean.base.NameValue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 任务状态，0禁用，1启动，2删除
 */
public enum JobStatusEnum {
    DISABLE(0, "禁用中"),
    ENABLE(1, "启用中"),
    DELETED(2, "已删除");

    private Integer value;
    private String name;

    JobStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static JobStatusEnum getItem(Integer value) {
        return Stream.of(values()).filter(item -> item.value.equals(value)).findAny().orElse(null);
    }

    public static List<NameValue> getItemList() {
        return Stream.of(values()).map(item -> new NameValue(item.name, item.value)).collect(Collectors.toList());
    }
}
