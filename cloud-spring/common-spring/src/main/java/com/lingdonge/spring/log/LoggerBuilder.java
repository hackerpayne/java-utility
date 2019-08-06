package com.lingdonge.spring.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 动态输出日志到指定文件
 * https://blog.csdn.net/weixin_42258128/article/details/81942796
 * https://codeday.me/bug/20171208/106430.html
 * <p>
 * 注：我们通常在日志文件路径上使用使用一些系统变量（比如${catalina.base}）、或者logback.xml配置的properties；此时我们可以使用OptionHelper辅助类来替换String中的变量占位符，此外LoggerContext中包含logback.xml配置的properties。不过这些properties默认上下文Scope为“local”，如果想让它们能够被LoggerContext访问，需要强制设置为“context”，这不会引入问题。
 * * <property scope="context" name="LOG_HOME" value="${catalina.base}/logs"/>
 */
public class LoggerBuilder {

    private static final Map<String, Logger> container = new HashMap<>();

    /**
     * 根据Logger的名称，Build出一个新的Logger对象
     *
     * @param name
     * @return
     */
    public Logger getLogger(String name) {
        Logger logger = container.get(name);
        if (logger != null) {
            return logger;
        }
        synchronized (LoggerBuilder.class) {
            logger = container.get(name);
            if (logger != null) {
                return logger;
            }
            logger = build(name);
            container.put(name, logger);
        }
        return logger;
    }

    /**
     * 生成一个ConsoleAppender
     *
     * @param name
     * @param level
     * @return
     */
    public static ConsoleAppender getConsoleAppender(String name, Level level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(context);

        // 这里设置级别过滤器
        LevelFilter levelFilter = getLevelFilter(level);
        levelFilter.start();

        PatternLayoutEncoder encoder = getPatternLayoutEncoder("", context);
        encoder.start();

        ConsoleAppender logConsoleAppender = new ConsoleAppender();
        logConsoleAppender.setContext(context);
        logConsoleAppender.setName("console");
        logConsoleAppender.setEncoder(encoder);
        logConsoleAppender.addFilter(levelFilter);
        logConsoleAppender.start();

        return logConsoleAppender;
    }


    /**
     * 通过传入的名字和级别，动态设置appender
     *
     * @param name
     * @param level
     * @return
     */
    public static RollingFileAppender getRollingAppender(String name, Level level) {

        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//        StatusPrinter.print(context);

        //这里设置级别过滤器
        LevelFilter levelFilter = getLevelFilter(level);
        levelFilter.start();

        PatternLayoutEncoder encoder = getPatternLayoutEncoder("", context);
        encoder.start();

        RollingFileAppender appender = new RollingFileAppender();
        appender.addFilter(levelFilter);
        appender.setContext(context); //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setName("FILE-" + name); //appender的name属性
        appender.setFile(OptionHelper.substVars(System.getProperty("user.dir") + "/logs/" + name + "/" + format.format(new Date()) + "/" + level.levelStr + ".log", context));//设置文件名
        appender.setAppend(true);
        appender.setPrudent(false);
        appender.setEncoder(encoder);

        TimeBasedRollingPolicy logFilePolicy = new TimeBasedRollingPolicy();
        logFilePolicy.setContext(context);
        logFilePolicy.setParent(appender);
        logFilePolicy.setFileNamePattern("logs/logfile-%d{yyyy-MM-dd_HH}.log");
        logFilePolicy.setMaxHistory(7);
        logFilePolicy.start();
        appender.setRollingPolicy(logFilePolicy);

        appender.start();
        return appender;
    }

    public static RollingFileAppender getRollingSizeAppender(String name, Level level) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//        StatusPrinter.print(context);

        // 这里设置级别过滤器
        LevelFilter levelFilter = getLevelFilter(level);
        levelFilter.start();

        PatternLayoutEncoder encoder = getPatternLayoutEncoder("", context);
        encoder.start();

        RollingFileAppender appender = new RollingFileAppender();
        appender.addFilter(levelFilter);
        appender.setContext(context); //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setName("FILE-" + name); //appender的name属性
        appender.setFile(OptionHelper.substVars(System.getProperty("user.dir") + "/logs/" + name + "/" + format.format(new Date()) + "/" + level.levelStr + ".log", context));//设置文件名
        appender.setAppend(true);
        appender.setPrudent(false);
        appender.setEncoder(encoder);

        //设置文件创建时间及大小的类
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
        policy.setFileNamePattern(OptionHelper.substVars(System.getProperty("user.dir") + "/logs/" + name + "/" + format.format(new Date()) + "/" + level.levelStr + "/.%d{yyyy-MM-dd}.%i.log", context)); //设置文件名模式
        policy.setMaxFileSize(FileSize.valueOf("128MB"));//最大日志文件大小
        policy.setMaxHistory(15);//设置最大历史记录为15条
        policy.setTotalSizeCap(FileSize.valueOf("32GB"));//总大小限制
        policy.setParent(appender); //设置父节点是appender
        policy.setContext(context);//设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        policy.start();
        appender.setRollingPolicy(policy);

        appender.start();
        return appender;
    }


    public static PatternLayoutEncoder getPatternLayoutEncoder(String pattern, LoggerContext context) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);//设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。

        if (StringUtils.isNotEmpty(pattern)) {
            encoder.setPattern(pattern);
        } else {
            //        encoder.setPattern("%d %p (%file:%line\\)- %m%n");//设置格式
            encoder.setPattern("%-12date{YYYY-MM-dd HH:mm:ss.SSS} %-5level - %msg%n");
//            encoder.setPattern("%d{yyyy-MM-dd/HH:mm:ss.SSS}|%X{localIp}|[%t] %-5level %logger{50} %line - %m%n");
        }

//        encoder.start();
        return encoder;
    }


    /**
     * 通过level设置过滤器
     *
     * @param level
     * @return
     */
    public static LevelFilter getLevelFilter(Level level) {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(level);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        return levelFilter;
    }

    /**
     * 内部构建多个Appender
     *
     * @param name
     * @return
     */
    private static Logger build(String name) {

        RollingFileAppender errorAppender = getRollingAppender(name, Level.ERROR);
        RollingFileAppender infoAppender = getRollingAppender(name, Level.INFO);
        RollingFileAppender warnAppender = getRollingAppender(name, Level.WARN);
        RollingFileAppender debugAppender = getRollingAppender(name, Level.DEBUG);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logger = context.getLogger("FILE-" + name);
        logger.setAdditive(false); // 设置不向上级打印信息
        logger.setLevel(Level.INFO);
        logger.addAppender(errorAppender);
        logger.addAppender(infoAppender);
        logger.addAppender(warnAppender);
        logger.addAppender(debugAppender);

        return logger;
    }

    public static void main(String[] args) {
        LoggerBuilder loggerBuilder = new LoggerBuilder();
        Logger logger = loggerBuilder.getLogger("test");
        logger.debug("shuai1 +++++++++++++++++++++++++++++++++++++debug");
        logger.warn("shuai2 +++++++++++++++++++++++++++++++++++++warn");
        logger.info("shuai3 +++++++++++++++++++++++++++++++++++++info");
        logger.error("shuai4 +++++++++++++++++++++++++++++++++++++error");

    }

}
