package com.lingdonge.core.util;

import com.lingdonge.core.regex.PatternPool;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JudgeUtil {


    /**
     * 不严格的手机号格式校验
     * 1开头+10位数字
     */
    public static boolean isMobile(String mobile) {
        String regex = "^1\\d{10}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 区号+座机号码
     *
     * @param fixedPhone
     * @return
     */
    public static boolean isFixedPhone(String fixedPhone) {
        String reg = "[0]{1}[0-9]{2,3}-[0-9]{7,8}";
        return Pattern.matches(reg, fixedPhone);
    }

    /**
     * 按照力蕴电话号码规则校验
     *
     * @param
     * @return
     */
    public static boolean isLyPhone(String phoneNumber) {
        String reg = "^((\\d{3,4})-?)(\\d{7,8})$";
        return Pattern.matches(reg, phoneNumber);
    }

    /**
     * 匹配中国邮政编码 6位数字
     *
     * @param postCode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isPostCode(String postCode) {
        String reg = "\\d{6}";
        return Pattern.matches(reg, postCode);
    }


    /**
     * 是否是数字
     *
     * @param number 待校验字符串
     * @return
     */
    public static boolean isNumber(String number) {
        String reg = "-?[0-9]|-?[0-9]+.?[0-9]+";
        return Pattern.matches(reg, number);
    }

    /**
     * 过滤字符串里面的非数字
     *
     * @param str
     * @return
     */
    public static String filterUnNumber(String str) {
        // 只允数字
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        //替换与模式匹配的所有字符（即非数字的字符将被""替换）
        return m.replaceAll("").trim();
    }

    /**
     * 判断是否为数字
     *
     * @param param
     * @return
     */
    public static boolean isDigit(String param) {
        return param.matches("[0-9]+");
    }

    /**
     * 判断是否为车牌号码
     *
     * @param param
     * @return
     */
    public static boolean isVehiclePlate(String param) {
        if ((param.matches("^(([\u4e00-\u9fa5]{1})|([A-Z]{1}))[A-Z]{1}[A-Z0-9]{4}(([\u4e00-\u9fa5]{1})|([A-Z0-9]{1}))$")
                || param.matches("^WJ[0-9]{2}(([\u4e00-\u9fa5]{1})|([0-9]{1})[0-9]{4})$"))
                && param.matches("^([\u4e00-\u9fa5]*[a-zA-Z0-9]+){6,}$") && param.matches("^.{3}((?!.*O)(?!.*I)).*$")) {
            return true;
        }
        return false;
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
        return param.matches("^[0-9]{6}$");
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
     * 判断是否为车辆识别代码（车架号）
     *
     * @param param
     * @return
     */
    public static boolean isIdentificationCode(String param) {
        return param.matches("^[A-Z0-9]{6,17}$");
    }

    /**
     * 判断是否为发动机号
     *
     * @param param
     * @return
     */
    public static boolean isVehicleEngineNo(String param) {
        return param.matches("^[A-Z0-9]+$");
    }

    /**
     * 判断是不是金额
     *
     * @param param
     * @return
     */
    public static boolean isMoney(String param) {
        return param.matches("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$");
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
     * 格式是yyyy/MM/dd HH:mm:ss
     *
     * @param param
     * @return boolean
     */
    public static boolean isValidDate(String param) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期
        try {
            format.setLenient(false);
            format.parse(param);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 格式是yyyy-MM-dd
     *
     * @param param
     * @return boolean
     */
    public static boolean isValidDay(String param) {
        boolean convertSuccess = true;
        // yyyy-MM-dd
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期
        try {
            format.setLenient(false);
            format.parse(param);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
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
