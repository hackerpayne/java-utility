package com.lindonge.core.util;

/**
 * Created by Kyle on 16/12/8.
 */
public class Utils {


    /**
     * 系统变量之换行符
     */
    public final static String LineSeparator = System.getProperty("line.separator");

    /**
     * 当前程序目录
     */
    public final static String CurrentDir = System.getProperty("user.dir");

    /**
     * 当前CLASS PATH路径
     */
    public final static String ClassPath = System.getProperty("java.class.path");

    /**
     * 当前系统名称：Win NT、Mac、Linux
     */
    public final static String CurrentPlatfrom = System.getProperty("os.name");

    /**
     * Jvt版本信息
     */
    public final static String JvmVersion = System.getProperty("sun.arch.data.bean");

    /**
     *
     */
    public final static String JavaLibruary = System.getProperty("java.library.path");


}
