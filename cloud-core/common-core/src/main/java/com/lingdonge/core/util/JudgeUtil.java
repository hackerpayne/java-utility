package com.lingdonge.core.util;

import cn.hutool.core.util.ReUtil;
import com.lingdonge.core.regex.PatternPool;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

@Slf4j
public class JudgeUtil {


    /**
     * 不严格的手机号格式校验
     * 1开头+10位数字
     */
    public static boolean isMobile(String mobile) {
        return ReUtil.isMatch(PatternPool.MOBILE, mobile);
    }

    /**
     * 区号+座机号码
     *
     * @param fixedPhone
     * @return
     */
    public static boolean isFixedPhone(String fixedPhone) {
        return ReUtil.isMatch(PatternPool.PHONE, fixedPhone);
    }

    /**
     * 匹配中国邮政编码 6位数字
     *
     * @param postCode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isPostCode(String postCode) {
        return ReUtil.isMatch(PatternPool.ZIP_CODE, postCode);
    }

    /**
     * 是否是数字
     *
     * @param number 待校验字符串
     * @return
     */
    public static boolean isNumber(String number) {
        return ReUtil.isMatch(PatternPool.NUMBERS, number);
    }

    /**
     * 过滤字符串里面的非数字
     *
     * @param str
     * @return
     */
    public static String filterUnNumber(String str) {
        return ReUtil.replaceAll(str, PatternPool.NOT_NUMBERS, "");
    }

    /**
     * 判断是否为数字
     *
     * @param param
     * @return
     */
    public static boolean isDigit(String param) {
        return ReUtil.isMatch(PatternPool.NUMBERS, param);
    }

    /**
     * 整数最多十位，小数最多两位
     *
     * @param param
     * @return
     */
    public static boolean isNumber12_2(String param) {
        return param.matches("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$");
    }

    /**
     * 整数最多8位，小数最多两位
     *
     * @param param
     * @return
     */
    public static boolean isNumber10_2(String param) {
        return param.matches("^(([1-9]\\d{0,7})|0)(\\.\\d{1,2})?$");
    }


    /**
     * 判断是否为邮政编码
     *
     * @param param
     * @return
     */
    public static boolean isZipCode(String param) {
        return ReUtil.isMatch(PatternPool.ZIP_CODE, param);
    }

    /**
     * 判断是否为电子邮箱
     *
     * @param param
     * @return
     */
    public static boolean isEmail(String param) {
        return param.matches(PatternPool.EMAIL2);
    }

    /**
     * 判断是否是有效的身份证号码
     *
     * @param input
     * @return
     */
    public static boolean isIdCardNo(String input) {
        return ReUtil.isMatch(PatternPool.CITIZEN_ID, input);
    }

    /**
     * 判断是不是金额
     *
     * @param param
     * @return
     */
    public static boolean isMoney(String param) {
        return ReUtil.isMatch(PatternPool.MONEY, param);
    }

    /**
     * 是否是有效可见的字符
     *
     * @param ch
     * @return
     */
    public static boolean isValidChar(char ch) {
        if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
            return true;
        }
        if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f)) {
            return true;// 简体中文汉字编码
        }

        return false;
    }

    /**
     * 判断是否有效的日期时间格式
     *
     * @param input
     * @param dateFormat
     * @return
     */
    public static boolean isValidDate(String input, String dateFormat) {
        boolean convertSuccess = true;

        try {
            // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期
            format.setLenient(false);
            format.parse(input);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 格式是yyyy/MM/dd HH:mm:ss
     *
     * @param param
     * @return boolean
     */
    public static boolean isValidDate(String param) {
        return isValidDate(param, "yyyy/MM/dd HH:mm:ss");
    }


    /**
     * 格式是yyyy-MM-dd
     *
     * @param param
     * @return boolean
     */
    public static boolean isValidDay(String param) {
        return isValidDate(param, "yyyy-MM-dd");
    }

    /**
     * 验证是否为URL
     *
     * @param value 值
     * @return 是否为URL
     */
    public static boolean isUrl(String value) {
        try {
            new java.net.URL(value);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    /**
     * 打印所有匹配到的结果
     *
     * @param matcher
     */
    public static void printMatches(Matcher matcher) {

        while (matcher.find()) {
            log.info("Full match: " + matcher.group(0));
            for (int i = 1; i <= matcher.groupCount(); i++) {
                log.info("Group [" + i + "]: " + matcher.group(i));
            }
        }
    }


}
