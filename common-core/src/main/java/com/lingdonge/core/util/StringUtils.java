package com.lingdonge.core.util;

import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 17/3/28.
 */
@Slf4j
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String EMPTY = "";


    private StringUtils() {
    }

    /**
     * 获取patter的过程较为负责,这里初始化时,做一次即可
     */
    private static Pattern pattern;

    static {
        pattern = Pattern.compile("((?<=\\{)([a-zA-Z_]{1,})(?=\\}))");
    }


    /**
     * 过滤emoji表情
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        return filterEmoji(source, "");
    }

    /**
     * emoji表情替换
     *
     * @param source  原字符串
     * @param slipStr emoji表情替换成的字符串
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source, String slipStr) {
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        } else {
            return source;
        }
    }

    /**
     * 给身份证或者姓名简单加密
     *
     * @param str
     * @param startLen
     * @param endLen
     * @return
     */
    public static String addStart(String str, int startLen, int endLen) {
        if (StringUtils.isNotBlank(str)) {
            int len = StringUtils.trim(str).length();

            switch (len) {
                case 2:
                    str = "*" + str.substring(1, 2);
                    break;
                case 3:
                    str = str.substring(0, 1) + "*" + str.substring(2, 3);
                    break;
                case 4:
                    str = str.substring(0, 2) + "*" + str.substring(3, 4);
                    break;
                default:
                    int startsCount = len - (startLen + endLen);
                    str = str.substring(0, startLen) + StringUtils.repeat("*", startsCount) + StringUtils.substring(str, str.length() - endLen, str.length());
                    break;
            }
        }
        return str;
    }


    /**
     * 隐藏姓名
     *
     * @param name
     * @return
     */
    public static String hideName(String name) {
        if (StringUtils.isNotBlank(name)) {
            name = name.trim();
            if (name.length() == 2) {
                name = StringUtils.substring(name, 0, 1) + "*";
            } else if (name.length() == 3) {
                name = StringUtils.substring(name, 0, 1) + "*" + StringUtils.substring(name, 1, 2);
            } else {
                String firstName = StringUtils.substring(name, 0, 1);
                String lastName = StringUtils.substring(name, name.length() - 1, name.length());
                String starts = "";
                for (int i = 1; i < name.length() - 1; i++) {
                    starts += "*";
                }
                name = firstName + starts + lastName;
            }
        }
        return name;
    }

    /**
     * 隐藏身份证号码
     *
     * @param idcard
     * @return
     */
    public static String hideIdcard(String idcard) {
        if (StringUtils.isNotBlank(idcard)) {
            String firstThree = StringUtils.substring(idcard, 0, 3);
            String lastFour = StringUtils.substring(idcard, idcard.length() - 4, idcard.length());
            String starts = "";
            for (int i = 3; i < idcard.length() - 4; i++) {
                starts += "*";
            }
            idcard = firstThree + starts + lastFour;
        }
        return idcard;
    }

    /**
     * 隐藏手机号
     *
     * @param mobile
     * @return
     */
    public static String hideMobile(String mobile) {
        if (StringUtils.isNotBlank(mobile)) {
            String firstThree = StringUtils.substring(mobile, 0, 3);
            String lastFour = StringUtils.substring(mobile, mobile.length() - 4, mobile.length());
            String starts = "";
            for (int i = 3; i < mobile.length() - 4; i++) {
                starts += "*";
            }
            mobile = firstThree + starts + lastFour;
        }
        return mobile;
    }

    /**
     * 批量替换变量
     *
     * @param text
     * @param map
     * @return
     */
    public String replaceV3(String text, Map<String, Object> map) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String key = matcher.group();
            text = text.replaceAll("\\{" + key + "\\}", map.get(key) + "");
        }

        return text;
    }

    /**
     * 批量替换变量为指定的结果
     *
     * @param text
     * @param mapList
     * @return
     */
    public List<String> replaceV4(String text, List<Map<String, Object>> mapList) {
        List<String> keys = new ArrayList<>();

        // 把文本中的所有需要替换的变量捞出来, 丢进keys
        Matcher matcher = pattern.matcher(text);
        int index = 0;
        while (matcher.find()) {
            String key = matcher.group();
            if (!keys.contains(key)) {
                keys.add(key);
                // 开始替换, 将变量替换成数字,
                text = text.replaceAll(keys.get(index), index + "");
                index++;
            }
        }


        List<String> result = new ArrayList<>();
        //  从map中将对应的值丢入 params 数组
        Object[] params = new Object[keys.size()];
        for (Map<String, Object> map : mapList) {
            for (int i = 0; i < keys.size(); i++) {
                params[i] = map.get(keys.get(i) + "");
            }

            result.add(replace(text, params));
        }
        return result;
    }

    public String replace(String text, Object... args) {
        return MessageFormat.format(text, args);
    }

    /**
     * @param str
     * @param searchChar
     * @return
     */
    public static boolean contains(String str, char searchChar) {
        return !isEmpty(str) && str.indexOf(searchChar) >= 0;
    }

    /**
     * @param str
     * @param searchStr
     * @return
     */
    public static boolean contains(String str, String searchStr) {
        return (str != null && searchStr != null) && str.contains(searchStr);
    }

    /**
     * 从List里面根据Contains进行匹配字符串，如果包含指定的字符串就清理掉这条记录
     * 比如：hahaha,12354，如果包含hah就清理，最后只会剩下一条记录12354
     * 例：removeDuplicateByContains(listLinks,new String[]{"m.liebiao.com","#","about.liebiao.com"});
     *
     * @param list
     * @param contains
     * @return
     */
    public static List<String> removeDuplicateByContains(List<String> list, String... contains) {
        // 遍历进行数据清洗
        Iterator<String> it = list.iterator();

        boolean isContains;
        while (it.hasNext()) {
            String x = it.next();
            isContains = false;

            for (String item : contains) {
                if (x.contains(item)) {
                    isContains = true;
                    break;
                }
            }

            if (isContains) {
                it.remove();
            }
        }
        return list;
    }

    /**
     * 移除字符串中所有给定字符串<br>
     * 例：removeAll("aa-bb-cc-dd", "-") =》 aabbccdd
     *
     * @param str         字符串
     * @param strToRemove 被移除的字符串
     * @return 移除后的字符串
     */
    public static String removeAll(String str, CharSequence strToRemove) {
        return str.replace(strToRemove, EMPTY);
    }

    /**
     * 去掉首部指定长度的字符串并将剩余字符串首字母小写<br>
     * 例如：str=setName, preLength=3 =》 return name
     *
     * @param str       被处理的字符串
     * @param preLength 去掉的长度
     * @return 处理后的字符串，不符合规范返回null
     */
    public static String removePreAndLowerFirst(CharSequence str, int preLength) {
        if (str == null) {
            return null;
        }

        if (str.length() > preLength) {
            char first = Character.toLowerCase(str.charAt(preLength));
            if (str.length() > preLength + 1) {
                return first + str.toString().substring(preLength + 1);
            }
            return String.valueOf(first);
        } else {
            return str.toString();
        }


    }

    /**
     * 清理HTML里面的多余标签
     *
     * @param htmlStr
     * @param length
     * @return
     */
    public static String delHTMLTag(String htmlStr, int length) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签
        htmlStr = checkStr(htmlStr);
        return htmlStr.replaceAll("\\s*", "").substring(0, length) + "..."; //返回文本字符串
    }

    /**
     * 删除(替换)不可见的unicode/utf-8字符
     *
     * @param str
     * @return
     */
    public static String checkStr(String str) {
        String s = null;
        char[] cc = str.toCharArray();
        for (int i = 0; i < cc.length; i++) {
            boolean b = JudgeUtil.isValidChar(cc[i]);
            if (!b) {
                cc[i] = ' ';
            }
        }
        s = String.valueOf(cc);
        return s.trim();
    }

    /**
     * 删除指定的开头的字符串
     *
     * @param stream
     * @param trim
     * @return
     */
    public static String trimStart(String stream, String trim) {
        // null或者空字符串的时候不处理
        if (stream == null || stream.length() == 0 || trim == null || trim.length() == 0) {
            return stream;
        }
        // 要删除的字符串结束位置
        int end;
        // 正规表达式
        String regPattern = "[" + trim + "]*+";
        Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
        // 去掉原始字符串开头位置的指定字符
        Matcher matcher = pattern.matcher(stream);
        if (matcher.lookingAt()) {
            end = matcher.end();
            stream = stream.substring(end);
        }
        // 返回处理后的字符串
        return stream;
    }

    /**
     * 判断是否所有数据都是非空，如果都是非空，返回true，任何一个是空的，返回false
     *
     * @param listDatas
     * @return
     */
    public static boolean isAllNotEmpty(String... listDatas) {
        boolean flag = true;
        for (String data : listDatas) {
            if (isEmpty(data)) {
                flag = false;
            }
        }
        return flag;
    }


}
