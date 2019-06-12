package com.lingdonge.core.util;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lingdonge.core.regex.PatternPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JudgeUtil {


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
            log.info("Full match: " + matcher.group(0));
            for (int i = 1; i <= matcher.groupCount(); i++) {
                log.info("Group [" + i + "]: " + matcher.group(i));
            }
        }
    }

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Map是否为空
     *
     * @param map 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    /**
     * 数组是否为空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回true<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回false<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回false，否则返回true
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final Object array) {
        if (null == array) {
            return true;
        } else if (false == isArray(array)) {
            //非数组，理解为此对象为数组的第一个元素
            return false;
        } else {
            return 0 == Array.getLength(array);
        }
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象
     * @throws NullPointerException 提供被监测的对象为<code>null</code>
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            throw new NullPointerException("Object check for isArray is null");
        }
        return obj.getClass().isArray();
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final long... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final int... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final short... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final char... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final byte... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final double... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final float... array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final boolean... array) {
        return array == null || array.length == 0;
    }

    // ---------------------------------------------------------------------- isNotEmpty

    /**
     * 数组是否为非空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为非空
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean isNotEmpty(final T... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回false<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回true<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回true，否则返回false
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final Object array) {
        return false == isEmpty((Object) array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final long... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final int... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final short... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final char... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final byte... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final double... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final float... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final boolean... array) {
        return (array != null && array.length != 0);
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 是否包含{@code null}元素
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNull(T... array) {
        if (isNotEmpty(array)) {
            for (T element : array) {
                if (null == element) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return null != map && false == map.isEmpty();
    }

    /**
     * 集合是否为非空
     *
     * @param collection 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return false == isEmpty(collection);
    }


    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isNotEmpty(Enumeration<?> enumeration) {
        return null != enumeration && enumeration.hasMoreElements();
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isEmpty(Enumeration<?> enumeration) {
        return null == enumeration || !enumeration.hasMoreElements();
    }

    /**
     * 数组是否为空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为空
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean isEmpty(final T... array) {
        return array == null || array.length == 0;
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }


}
