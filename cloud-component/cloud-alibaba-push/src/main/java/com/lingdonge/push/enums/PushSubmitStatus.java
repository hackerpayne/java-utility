package com.lingdonge.push.enums;

public enum PushSubmitStatus {

    SUCCESS(1, "推送成功"),
    FAIL(0, "推送失败"),
    ;

    private Integer value;
    private String name;

    private PushSubmitStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
		return value;
	}
    
    /**
     * 获取名称
     *
     * @param value
     * @return
     */
    public static PushSubmitStatus getName(Integer value) {
        for (PushSubmitStatus item : values()) {
            if (value != null && item.value == value) {
                return item;
            }
        }
        return null;
    }


}
