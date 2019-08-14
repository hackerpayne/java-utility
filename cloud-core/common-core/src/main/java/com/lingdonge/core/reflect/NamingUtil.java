package com.lingdonge.core.reflect;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 驼峰转换
 */
public class NamingUtil {

    private static final String UNDERLINE = "_";

    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    public static String getFirstUpperName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        String firstChar = StringUtils.substring(name, 0, 1).toUpperCase();
        return firstChar + StringUtils.substring(name, 1);
    }

    /**
     * 首字母小写
     *
     * @param name
     * @return
     */
    public static String getFirstLowerName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        String firstChar = StringUtils.substring(name, 0, 1).toLowerCase();
        return firstChar + StringUtils.substring(name, 1);
    }

    /**
     * 利用正则表达式极简转换驼峰到下划线
     *
     * @param camelName
     * @return
     */
    public static String camelToUnderlineNew(String camelName) {
        return camelName.replaceAll("([A-Z]+)", "_$1").toLowerCase();
    }

    /**
     * 驼峰转下划线  camelToUnderline -> camel_to_underline
     *
     * @param param 驼峰形式的字符串
     * @return 下划线形式的字符串
     */
    public static String camelToUnderline(String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        String temp = sb.toString();
        if (temp.startsWith(UNDERLINE)) {
            return temp.substring(1);
        }
        return temp;

    }

    final static Pattern pattern = Pattern.compile("(_[a-z]{1})");

    /**
     * 利用正则极简转换下划线为驼峰命名
     *
     * @param underlineName
     * @return
     */
    public static String underlineToCamelNew(String underlineName) {

        Matcher matcher = pattern.matcher(underlineName);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group(1);
            matcher.appendReplacement(result, replacement.replace("_", "").toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString().replace("_", "");
    }

    /**
     * 下划线转驼峰  underline_to_camel -> underlineToCamel
     *
     * @param param 下划线形式的字符串
     * @return 驼峰形式的字符串
     */
    public static String underlineToCamel(String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == '_') {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

}
