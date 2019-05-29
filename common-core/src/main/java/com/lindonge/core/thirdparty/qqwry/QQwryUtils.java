package com.lindonge.core.thirdparty.qqwry;

import com.lindonge.core.util.Utils;
import com.lindonge.core.model.ModelIPLocation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class QQwryUtils {
    private static final Logger logger = LoggerFactory.getLogger(QQwryUtils.class);

    private static QQwryParser parser;
    private static File qqwryFile = null;

    /**
     * 同步代码，以支持多线程
     *
     * @return
     */
    private synchronized static QQwryParser getQQwryParser() {
        if (parser != null) {
            return parser;
        }

        if (qqwryFile == null) {
            qqwryFile = FileUtils.getFile(Utils.CurrentDir, "conf", "QQWry.DAT");
        }

        try {
            parser = new QQwryParser(qqwryFile);
        } catch (Exception e) {
            logger.error("getQQwryParser发生异常", e);
            parser = null;
        }

        return parser;
    }

    /**
     * 设置数据文件地址
     *
     * @param file
     */
    public static void setQQwryFile(File file) {
        qqwryFile = file;
        if (parser != null) {
            parser.close();
            parser = null;
        }
    }

    /**
     * 获取IP地址信息
     *
     * @param ip
     * @return
     */
    public static IPAddrInfo getAddrInfo(String ip) {
        QQwryParser parser = getQQwryParser();
        if (parser != null) {
            return parser.getAddrInfo(ip);
        }
        return null;
    }

    /**
     * 获取IP信息转化为通用的类描述
     *
     * @param ip
     * @return
     */
    public static ModelIPLocation getIPLocation(String ip) {
        IPAddrInfo address = getAddrInfo(ip);
        ModelIPLocation location = new ModelIPLocation();
        location.setIp(ip);
        location.setCity(address.getCity());
        location.setCountry(address.getCountry());
        location.setProvince(address.getProvince());
        location.setIsp(address.getProvider());
        return location;
    }

    /**
     * 获取服务商信息
     *
     * @param ip
     * @return
     */
    public static String getProvider(String ip) {
        return getAddrInfo(ip).getProvider();
    }

    /**
     * 获取国家信息
     *
     * @param ip
     * @return
     */
    public static String getCountry(String ip) {
        return getAddrInfo(ip).getCountry();
    }

    /**
     * 获取省份信息
     *
     * @param ip
     * @return
     */
    public static String getProvince(String ip) {
        return getAddrInfo(ip).getProvince();
    }

    /**
     * 获取城市信息
     *
     * @param ip
     * @return
     */
    public static String getCity(String ip) {
        return getAddrInfo(ip).getCity();
    }


}