package com.lingdonge.http.faker;

import com.google.common.collect.Lists;
import com.lingdonge.core.regex.ReUtil;
import com.lingdonge.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码处理类
 */
@Slf4j
public class MobileHelper {

    private final static Pattern pattern = Pattern.compile("[^0-9]");

    /**
     * 简单判断号码，1开头，11位数字即可
     */
    public final static String SIMPLE_MOBILE = "1\\d{10}";

    /**
     * 简单判断号码，前后不能有别的字符
     */
    public final static String SIMPLE_MOBILE_STRICT = "^1\\d{10}$";

    /**
     * 中国移动,10648开头的为13位的物联网号码
     */
    public final static String CHINA_MOBILE = "^1(3[4-9]|47|5[012789]|78|8[23478])\\d{8}$|^170[356]\\d{7}$|^10648\\d{8}$";

    /**
     * 中国联通
     */
    public final static String CHINA_UNICOM = "^1(3[0-2]|45|5[56]|7[156]|8[56])\\d{8}|^170[4789]\\d{7}$|^10646\\d{8}$";

    /**
     * 中国电信
     */
    public final static String CHINA_TELICOM = "^1([35]3|7[37]|8[019])\\d{8}$|^170[012]\\d{7}$|^10649\\d{8}$";

    /**
     * 简单判断中国座机号码，0开头，3位-后面8位数
     */
    public final static String LOCAL_PHONE = "0\\d{2,4}-\\d{8}";

    /**
     * 判断是否为手机号，只判断1开头，11位数字
     *
     * @param param
     * @return
     */
    public static boolean isMobile(String param) {
        if (param.matches(SIMPLE_MOBILE)) {
            return true;
        }
        return false;
    }

    /**
     * 三网判断
     * 判断是否是有效的中国号码，电信移动和联通的
     *
     * @param phone
     * @return
     */
    public static boolean isValidMobile(String phone) {
        if (phone.matches(CHINA_MOBILE) || phone.matches(CHINA_TELICOM) || phone.matches(CHINA_UNICOM))
            return true;
        return false;
    }

    /**
     * 匹配内容里面的所有手机号码，以及座机
     *
     * @param input      页面或者HTML内容
     * @param matchPhone 是否匹配座机号码
     * @return
     */
    public static List<String> getMobileList(String input, boolean matchPhone) {
        List<String> listResults = Lists.newArrayList();

        Pattern pattern = Pattern.compile(SIMPLE_MOBILE, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input);
        String matches = "";
        while (matcher.find()) {
            matches = matcher.group(0);
            if (StringUtils.isNotEmpty(matches) && !listResults.contains(matches)) {
                listResults.add(matches);
            }
        }

        if (matchPhone) {
            pattern = Pattern.compile(LOCAL_PHONE, Pattern.MULTILINE);
            matcher = pattern.matcher(input);
            while (matcher.find()) {
                matches = matcher.group(0);
                if (StringUtils.isNotEmpty(matches) && !listResults.contains(matches)) {
                    listResults.add(matches);
                }
            }
        }

        return listResults;
    }

    /**
     * 从字符串里面匹配出手机号码
     *
     * @param input
     * @return
     */
    public static String matchMobile(String input) {
        return ReUtil.get(SIMPLE_MOBILE, input, 0);
    }

    /**
     * 从字符串中匹配座机号码
     *
     * @param input
     * @return
     */
    public static String matchPhone(String input) {
        return ReUtil.get("(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{8}", input, 0);
    }

    /**
     * 格式化清理Mobile手机号码，包括非数字字符，0086、086等
     *
     * @param input
     * @return
     */
    public static String clearMobile(String input) {

        if (StringUtils.isEmpty(input)) {
            return "";
        }

        input = pattern.matcher(input).replaceAll("");//清理所有非数字的字段，包括-前后的空格等

        // 除一些肯定不要的
        if (input.startsWith("400")) {
            return "";
        }

        //清理0086开头
        if (input.startsWith("0086")) {
            input = StringUtils.trimStart(input, "0086");
        }
        if (input.startsWith("086")) {
            input = StringUtils.trimStart(input, "086");
        }
        if (input.startsWith("86")) {
            input = StringUtils.trimStart(input, "86");
        }

        return input;
    }

    public enum MobileEnum {
        NONE(0), //未知,调用构造函数来构造枚举项
        CHINA_MOBILE(1),//移动
        UNICOM(2), //联通
        TELECOM(3); //电信

        private int value = 0;

        private MobileEnum(int value) {   //   必须是private的，否则编译错误
            this.value = value;
        }

        public static MobileEnum valueOf(int value) {   //   手写的从int到enum的转换函数
            switch (value) {
                case 1:
                    return CHINA_MOBILE;
                case 2:
                    return UNICOM;
                case 3:
                    return TELECOM;
                default:
                    return NONE;
            }
        }

        public int value() {
            return this.value;
        }

    }

    /**
     * 判断手机号码的网络类型：移动、联通、电信
     * mobile_pattern是移动
     * unicom_pattern是联通
     * telecom_pattern是电信
     * mobile_pattern=^1(3[4-9]|47|5[012789]|78|8[23478])\\d{8}$|^170[356]\\d{7}$|^10648\\d{8}$
     * unicom_pattern=^1(3[0-2]|45|5[56]|7[156]|8[56])\\d{8}|^170[4789]\\d{7}$|^10646\\d{8}$
     * telecom_pattern=^1([35]3|7[37]|8[019])\\d{8}$|^170[012]\\d{7}$|^10649\\d{8}$
     *
     * @param mobileStr
     * @return
     */
    public static MobileEnum getMobileType(String mobileStr) {

        MobileEnum mobileEnum = MobileEnum.NONE;

        if (mobileStr.matches(CHINA_MOBILE)) {
            mobileEnum = MobileEnum.CHINA_MOBILE;
        } else if (mobileStr.matches(CHINA_UNICOM)) {
            mobileEnum = MobileEnum.UNICOM;
        } else if (mobileStr.matches(CHINA_TELICOM)) {
            mobileEnum = MobileEnum.TELECOM;
        }

        return mobileEnum;
    }

}
