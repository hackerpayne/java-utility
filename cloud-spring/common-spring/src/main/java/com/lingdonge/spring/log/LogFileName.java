package com.lingdonge.spring.log;

public enum LogFileName {

    SYS_LOG("sysLog"),
    BIZ_LOG("bizLog");

    private String logFileName;

    LogFileName(String fileName) {
        this.logFileName = fileName;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public static LogFileName getAwardTypeEnum(String value) {
        LogFileName[] arr = values();
        for (LogFileName item : arr) {
            if (null != item && !item.logFileName.equals("")) {
                return item;
            }
        }
        return null;
    }
}
