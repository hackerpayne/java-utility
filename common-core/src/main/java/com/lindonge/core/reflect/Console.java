package com.lindonge.core.reflect;

import com.lindonge.core.util.StringUtils;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * 命令行（控制台）工具方法类<br>
 * 此类主要针对{@link System#out} 和 {@link System#err} 做封装。
 *
 * @author Looly
 */
public final class Console {

    private Console() {
    }

    //--------------------------------------------------------------------------------- Log

    /**
     * 同 System.out.println()方法，打印控制台日志
     */
    public static void log() {
        out.println();
    }

    /**
     * 同 System.out.println()方法，打印控制台日志
     *
     * @param obj 要打印的对象
     */
    public static void log(Object obj) {
        if (obj instanceof Throwable) {
            Throwable e = (Throwable) obj;
            log(e, e.getMessage());
        } else {
            log("{}", obj);
        }
    }

    /**
     * 同 System.out.println()方法，打印控制台日志
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   值
     */
    public static void log(String template, Object... values) {
        log(null, template, values);
    }

    /**
     * 同 System.out.println()方法，打印控制台日志
     *
     * @param t        异常对象
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   值
     */
    public static void log(Throwable t, String template, Object... values) {
        out.println(StringUtils.format(template, values));
        if (null != t) {
            t.printStackTrace();
            out.flush();
        }
    }

    //--------------------------------------------------------------------------------- Error

    /**
     * 同 System.err.println()方法，打印控制台日志
     */
    public static void error() {
        err.println();
    }

    /**
     * 同 System.err.println()方法，打印控制台日志
     *
     * @param obj 要打印的对象
     */
    public static void error(Object obj) {
        if (obj instanceof Throwable) {
            Throwable e = (Throwable) obj;
            error(e, e.getMessage());
        } else {
            error("{}", obj);
        }
    }

    /**
     * 同 System.err.println()方法，打印控制台日志
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   值
     */
    public static void error(String template, Object... values) {
        error(null, template, values);
    }

    /**
     * 同 System.err.println()方法，打印控制台日志
     *
     * @param t        异常对象
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   值
     */
    public static void error(Throwable t, String template, Object... values) {
        err.println(StringUtils.format(template, values));
        if (null != t) {
            t.printStackTrace(err);
            err.flush();
        }
    }
}
