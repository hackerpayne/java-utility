package com.lingdonge.core.thirdparty.qqwry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * 对IP地址进行编码的类
 * Created by kyle on 17/3/1.
 */
public class IPEncode {

    private static final Logger logger = LoggerFactory.getLogger(IPEncode.class);

    /**
     * IP转byte
     *
     * @param ip
     * @return
     */
    public static byte[] encode(String ip) {
        byte[] ret = new byte[4];
        java.util.StringTokenizer st = new java.util.StringTokenizer(ip, ".");
        try {
            ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("IP编码出错", e);
        }
        return ret;
    }

    /**
     * @param s
     * @param srcEncoding
     * @param destEncoding
     * @return
     */
    public static String codingFormat(String s, String srcEncoding, String destEncoding) {
        try {
            return new String(s.getBytes(srcEncoding), destEncoding);
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    /**
     * Byte转String
     *
     * @param b
     * @param encoding
     * @return
     */
    public static String bytesToString(byte[] b, String encoding) {
        try {
            return new String(b, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b);
        }
    }

    /**
     * Byte转String
     *
     * @param b
     * @param offset
     * @param len
     * @param encoding
     * @return
     */
    public static String bytesToString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }

    /**
     * Byte格式的IP转字符串
     *
     * @param ip
     * @return
     */
    public static String decode(byte[] ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(ip[0] & 0xFF);
        sb.append('.');
        sb.append(ip[1] & 0xFF);
        sb.append('.');
        sb.append(ip[2] & 0xFF);
        sb.append('.');
        sb.append(ip[3] & 0xFF);
        return sb.toString();
    }

    /**
     * IP转数字格式
     *
     * @param s
     * @return
     */
    public static long toNumericIP(final String s) {
        final String[] parts = s.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("ip=" + s);
        }
        long n = Long.parseLong(parts[0]) << 24L;
        n += Long.parseLong(parts[1]) << 16L;
        n += Long.parseLong(parts[2]) << 8L;
        n += Long.parseLong(parts[3]);
        return n;
    }


}
