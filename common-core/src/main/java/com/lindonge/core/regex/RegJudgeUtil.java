package com.lindonge.core.regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kyle on 16/11/13.
 */
public class RegJudgeUtil {

    private static final Logger logger = LoggerFactory.getLogger(RegJudgeUtil.class);

    public static void main(String[] args) {

//        String mobile = RegJudgeUtil.matchMobile("<title>【曹春泉（个体经营）联系方式】_曹春泉_13146418333-马可波罗 </title>");

//        String mobile = RegJudgeUtil.matchPhone("【台州市椒江欣居塑胶制品厂联系方式】_王吕松 &nbsp_0576-88991872-马可波罗 ");
//        System.out.println("手机号码为：" + mobile);

        // 匹配页面里面的所有号码信息
//        List<String> phones = RegJudgeUtil.getMobileList(new HttpHelper().getHtml("http://blfssy.shop.liebiao.com/"), true);
//        System.out.println(phones);
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
        if (param.matches("[0-9]+")) {
            return true;
        }
        return false;
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
        if (param.matches("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$")) {
            return true;
        }
        return false;
    }

    /**
     * 整数最多8位，小数最多两位
     *
     * @param param
     * @return
     */
    public static boolean isNumber10_2(String param) {
        if (param.matches("^(([1-9]\\d{0,7})|0)(\\.\\d{1,2})?$")) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否为邮政编码
     *
     * @param param
     * @return
     */
    public static boolean isZipCode(String param) {
        if (param.matches("^[0-9]{6}$")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为电子邮箱
     *
     * @param param
     * @return
     */
    public static boolean isEmail(String param) {
        if (param.matches(PatternPool.EMAIL2)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为车辆识别代码（车架号）
     *
     * @param param
     * @return
     */
    public static boolean isIdentificationCode(String param) {
        if (param.matches("^[A-Z0-9]{6,17}$")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为发动机号
     *
     * @param param
     * @return
     */
    public static boolean isVehicleEngineNo(String param) {
        if (param.matches("^[A-Z0-9]+$")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是不是金额
     *
     * @param param
     * @return
     */
    public static boolean isMoney(String param) {
        if (param.matches("^(([1-9]\\d{0,9})|0)(\\.\\d{1,2})?$")) {
            return true;
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
     * 打印所有匹配到的结果
     *
     * @param matcher
     */
    public static void printMatches(Matcher matcher) {

        while (matcher.find()) {
            logger.info("Full match: " + matcher.group(0));
            for (int i = 1; i <= matcher.groupCount(); i++) {
                logger.info("Group [" + i + "]: " + matcher.group(i));
            }
        }
    }
}
