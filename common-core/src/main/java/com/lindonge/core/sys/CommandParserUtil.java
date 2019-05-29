package com.lindonge.core.sys;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

/**
 * 命令行解析类
 */
@Slf4j
public class CommandParserUtil {

    private CommandLine commandLine;

    /**
     * 传入收到的命令行和需要解析的字段列表
     *
     * @param args
     * @param params
     */
    public CommandParserUtil(String[] args, String[] params) {
        // Create a Parser
        CommandLineParser parser = new DefaultParser();

        final Options options = new Options();
//        options.addOption("proxy", false, "使用Proxy代理信息");
//        options.addOption("enable_bloom", false, "是否把MySQL采集列表更新到Bloom进行去重");
//        options.addOption("enable_keywords_file", false, "是否添加关键词到采集列表");

        for (String item : params) {
            options.addOption(OptionBuilder.withLongOpt(item).withValueSeparator('=').hasArg().create());
        }
//        options.addOption(OptionBuilder.withLongOpt("proxy").withValueSeparator('=').hasArg().create());
//        options.addOption(OptionBuilder.withLongOpt("enable_bloom").withValueSeparator('=').hasArg().create());
//        options.addOption(OptionBuilder.withLongOpt("enable_keywords_file").withValueSeparator('=').hasArg().create());

        // 解析Args参数列表
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            log.error("CommandParserUtil解析出错", e);
        }

    }


    /**
     * 取出从命令行解析出来的值信息
     *
     * @param param
     * @return
     */
    public String getValue(String param, String defaultValue) {
        if (commandLine != null && commandLine.hasOption(param)) {
            return commandLine.getOptionValue(param);
        }
        return defaultValue;
    }

    /**
     * 取出命令行解析出来的True/Flase值
     *
     * @param param
     * @param defaultValue
     * @return
     */
    public Boolean getBoolValue(String param, Boolean defaultValue) {
        if (commandLine != null && commandLine.hasOption(param)) {
            return Boolean.valueOf(commandLine.getOptionValue(param));
        }
        return defaultValue;
    }


}
