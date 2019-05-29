package com.lindonge.core.http;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.Assert;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class RequestUtil {
    /**
     * 参数为List时，按ASCII排序,连接参数转换为String字符串
     * 常用于签名排序参数时使用
     *
     * @param list List参数列表
     * @return
     */
    public static String listToSortQueryString(ArrayList<String> list) {

        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        // 构造签名键值对的格式
        StringBuilder sb = new StringBuilder();
        for (String ss : list) {
            sb.append(ss + "&");
        }
        return sb.toString();
    }

    /**
     * Map转QueryString
     *
     * @param mapParams
     * @return
     */
    public static String mapToQueryStr(Map<String, Object> mapParams) {
        return mapParams.entrySet().stream()
                .map(p -> UrlUtils.encodeUtf8(p.getKey()) + "=" + UrlUtils.encodeUtf8(p.getValue().toString()))
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");
    }

    /**
     * 将map型转为请求参数型
     *
     * @param data
     * @return
     */
    public static String mapToQueryString(Map<String, String> data) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry i : data.entrySet()) {
            sb.append(i.getKey()).append("=").append(UrlUtils.encodeUtf8(i.getValue() + "")).append("&");
        }

        if (sb.toString().endsWith("&")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 参数为Map时按ASCII字典排序，生成string格式
     *
     * @param map Map参数列表
     * @return
     */
    public static String mapToSortedQueryString(Map map) {

        String result = "";
        try {
            List<Map.Entry<?, ?>> infoIds = new ArrayList<Map.Entry<?, ?>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            infoIds.sort(new Comparator<Map.Entry<?, ?>>() {
                @Override
                public int compare(Map.Entry<?, ?> o1, Map.Entry<?, ?> o2) {
                    return (o1.getKey().toString()).compareTo(o2.getKey().toString());
                }
            });
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<?, ?> item : infoIds) {
                if (StringUtils.isNotEmpty(item.getKey().toString())) {
                    String key = item.getKey().toString();
                    String val = item.getValue().toString();
                    if (StringUtils.isNotEmpty(val)) {
                        sb.append(key).append("=").append(val).append("&");
                    }
                }
            }
            if (sb.toString().endsWith("&")) {
                sb.deleteCharAt(sb.length() - 1);
            }

            result = sb.toString();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * List转换为Query查询字符串
     *
     * @param basicNameValuePairs
     * @return
     */
    public static String paramToQueryString(List<BasicNameValuePair> basicNameValuePairs) {
        StringBuffer result = new StringBuffer();
        int index = 0;
        if (basicNameValuePairs != null && basicNameValuePairs.size() != 0) {
            for (BasicNameValuePair basicNameValuePair : basicNameValuePairs) {
                if (index == 0) {
                    result.append("?");
                } else {
                    result.append("&");
                }
                result.append(basicNameValuePair.getName() + "=" + basicNameValuePair.getValue());
                index++;
            }
        }
        return result.toString();
    }

    /**
     * 将request中的参数转换成Map
     *
     * @param request
     * @return
     */
    public static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = Maps.newHashMap();
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;

            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.toString().substring(1));
            } else {
                retMap.put(name, "");
            }
        }
        return retMap;
    }

    /**
     * 获取请求体HttpServletRequest内容到Map内
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Map<String, Object> getParamsFromRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();

        StringBuilder builder = new StringBuilder();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String bodyString = builder.toString();
            return JSON.parseObject(bodyString, Map.class); // 读取String到Map里面
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    /**
     * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
     * <p>
     * 返回的结果的Parameter名已去除前缀.
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Object> getParametersWith(ServletRequest request, String prefix) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        String pre = prefix;
        if (pre == null) {
            pre = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(pre) || paramName.startsWith(pre)) {
                String unprefixed = paramName.substring(pre.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    values = new String[]{};
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    public static String getBodyString(final ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 复制输入流
     *
     * @param inputStream
     * @return</br>
     */
    public static InputStream cloneInputStream(ServletInputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }

    /**
     * 组合Parameters生成Query String的Parameter部分,并在paramter name上加上prefix.
     *
     * @param params
     * @param prefix
     * @return
     */
    public static String encodeParameterWithPrefix(Map<String, Object> params, String prefix) {
        StringBuilder queryStringBuilder = new StringBuilder();

        String pre = prefix;
        if (pre == null) {
            pre = "";
        }
        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            queryStringBuilder.append(pre).append(entry.getKey()).append("=").append(entry.getValue());
            if (it.hasNext()) {
                queryStringBuilder.append("&");
            }
        }
        return queryStringBuilder.toString();
    }

    /**
     * <p>
     * 获取客户端的IP地址的方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。
     * 但是在通过了Apache,Squid等反向代理软件就不能获取到客户端的真实IP地址了，如果通过了多级反向代理的话，
     * X-Forwarded-For的值并不止一个，而是一串IP值， 究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 例如：X-Forwarded-For：192.168.1.110, 192.168.1.120,
     * 192.168.1.130, 192.168.1.100 用户真实IP为： 192.168.1.110
     * </p>
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String unknown = "unknown";

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        /*
          对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length() =
          15
         */
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

}
