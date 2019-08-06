package com.lingdonge.push.enums;

public enum PushCodeSenderEnum {
    ALI("ali", "阿里"),
    JIGUANG("jiguang", "极光"),
    ;

    private String value;
    private String name;

    private PushCodeSenderEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    /**
     * 获取名称
     *
     * @param value
     * @return
     */
    public static PushCodeSenderEnum getName(String value) {
        for (PushCodeSenderEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }

}
