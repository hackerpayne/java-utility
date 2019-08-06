package com.lingdonge.core.http;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Http请求工具类
 */
@Slf4j
public class HtmlUtil {

    /**
     * 获取客户端IP<br>
     * 默认检测的Header：<br>
     * 1、X-Forwarded-For<br>
     * 2、X-Real-IP<br>
     * 3、ModelProxy-Client-IP<br>
     * 4、WL-ModelProxy-Client-IP<br>
     * otherHeaderNames参数用于自定义检测的Header
     *
     * @param request          请求对象
     * @param otherHeaderNames 其他自定义头文件
     * @return IP地址
     */
    public static String getClientIP(javax.servlet.http.HttpServletRequest request, String... otherHeaderNames) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "ModelProxy-Client-IP", "WL-ModelProxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (ArrayUtil.isNotEmpty(otherHeaderNames)) {
            headers = ArrayUtil.addAll(headers, otherHeaderNames);
        }

        String ip;
        for (String header : headers) {
            ip = request.getHeader(header);
            if (!isUnknow(ip)) {
                return getMultistageReverseProxyIp(ip);
            }
        }

        ip = request.getRemoteAddr();
        return getMultistageReverseProxyIp(ip);
    }

    /**
     * 检测是否https
     *
     * @param url URL
     * @return 是否https
     */
    public static boolean isHttps(String url) {
        return url.toLowerCase().startsWith("https");
    }


    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (false == isUnknow(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关<br>
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknow(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 根据文件扩展名获得MimeType
     *
     * @param filePath 文件路径或文件名
     * @return MimeType
     */
    public static String getMimeType(String filePath) {
        return URLConnection.getFileNameMap().getContentTypeFor(filePath);
    }
    // ----------------------------------------------------------------------------------------- Private method start


    /**
     * Map转换为List Parameter
     * Map转换为List列表的参数
     *
     * @param basicNameValueMap
     * @return
     */
    public static List<BasicNameValuePair> MapToList(Map<String, String> basicNameValueMap) {
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
        // 转换一下map 给list
        if (basicNameValueMap != null && basicNameValueMap.size() > 0) {
            Set<String> keySet = basicNameValueMap.keySet();
            BasicNameValuePair basicNameValuePair;
            for (String key : keySet) {
                basicNameValuePair = new BasicNameValuePair(key, basicNameValueMap.get(key));
                basicNameValuePairs.add(basicNameValuePair);
            }
        }
        return basicNameValuePairs;
    }

    /**
     * 输入流转换为字符串
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String streamToStr(InputStream in) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 删除A标签，只删除简单的A标签，带属性的
     *
     * @param input
     * @return
     */
    public static String clearATag(String input) {
//        return input.replaceAll( "</?a>", "" );
        return input.replaceAll("</?a.*?>", "");
    }

    /**
     * 获取 ：<meta http-equiv="refresh" content="0; url=http://bbs.moonseo.cn/" />的跳转地址
     *
     * @param input
     * @return
     */
    public static String getMetaRefresh(String input) {
        String metaUrl = ReUtil.get("<(?:META|meta|Meta) (?:HTTP-EQUIV|http-equiv)=\"refresh\".*(URL|url)=(.*)\"", input, 2);

        metaUrl = StringUtils.removeStart(metaUrl, "'");
        metaUrl = StringUtils.removeEnd(metaUrl, "'");

        return metaUrl;
    }

}