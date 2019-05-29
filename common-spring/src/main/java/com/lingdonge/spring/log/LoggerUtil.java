package com.lingdonge.spring.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指定日志打印到指定目录下面，使用时：
 * 1、在logback.xml中配置日志及格式
 * <!-- 不同的业务逻辑日志打印到指定文件夹-->
 * <logger name="bizLog" additivity="false" level="INFO">
 * <appender-ref ref="bizLogAppender"/>
 * </logger>
 * <logger name="sysLog" additivity="false" level="INFO">
 * <appender-ref ref="sysLogAppender"/>
 * </logger>
 * <p>
 * 2、代码中使用
 * private static final Logger sys_Log = LoggerUtils.Logger(LogFileName.SYS_LOG);
 */
public class LoggerUtil {

    public static <T> Logger Logger(Class<T> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 打印到指定的文件下
     *
     * @param desc 日志文件名称
     * @return
     */
    public static Logger Logger(LogFileName desc) {
        return LoggerFactory.getLogger(desc.getLogFileName());
    }

}
