package com.lingdonge.core.encode;

import com.lingdonge.core.util.StringUtils;

import java.io.*;

/**
 * 编码处理辅助类
 */
public class EncodeUtil {

    public static void main(String[] args) {
        String unicode = EncodeUtil.str2Unicode("中文");

        System.out.println(unicode);

        String decode = EncodeUtil.unicode2Str(unicode);

        System.out.println("还原结果：" + decode);
    }

    /**
     * @param string
     * @return
     */
    public static String str2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * @param unicode
     * @return
     */
    public static String unicode2Str(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    /**
     * InputStream转换为String字符串
     *
     * @param in
     * @param encode
     * @return
     */
    public static String inputStreamToStr(InputStream in, String encode) {

        String str = "";
        try {
            if (StringUtils.isEmpty(encode)) {
                // 默认以utf-8形式
                encode = "utf-8";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, encode));
            StringBuffer sb = new StringBuffer();

            while ((str = reader.readLine()) != null) {
                sb.append(str).append("\n");
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * 利用byte数组转换InputStream------->String
     *
     * @param in
     * @param encode
     * @return
     */
    public static String inputStreamToByteStr(InputStream in, String encode) {
        StringBuffer sb = new StringBuffer();
        byte[] b = new byte[1024];
        int len = 0;
        try {
            if (encode == null || encode.equals("")) {
                // 默认以utf-8形式
                encode = "utf-8";
            }
            while ((len = in.read(b)) != -1) {
                sb.append(new String(b, 0, len, encode));
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 字符串转换为InputStream
     *
     * @param inStr
     * @return
     */
    public static InputStream strToInputStream(String inStr) {
        try {
            return new ByteArrayInputStream(inStr.getBytes());
            // return new ByteArrayInputStream(inStr.getBytes("UTF-8"));
//            return new StringBufferInputStream(inStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
