package com.lindonge.core.reflect;

import org.apache.commons.lang3.StringUtils;

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
     * 驼峰转下划线  camelToUnderline -> camel_to_underline
     *
     * @param param 驼峰形式的字符串
     * @return 下划线形式的字符串
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
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

    /**
     * 下划线转驼峰  underline_to_camel -> underlineToCamel
     *
     * @param param 下划线形式的字符串
     * @return 驼峰形式的字符串
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
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


    public static void main(String[] args) {
        System.out.println(camelToUnderline("a.userName"));
        System.out.println(underlineToCamel("a_user_Name"));
    }
}
