package com.lingdonge.spring.enums;

import com.lingdonge.core.bean.base.NameValue;

import java.util.ArrayList;
import java.util.List;

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
            if (value != null && item.value == value) {
                return item.name;
            }
        }
        return "未知类型-" + value;
    }

    public static List<NameValue> getItemList() {
        List<NameValue> dataList = new ArrayList<NameValue>();
        for (YesNoEnum item : YesNoEnum.values()) {
            dataList.add(new NameValue(item.name, item.value));
        }
        return dataList;
    }
}
