package com.lingdonge.core.http;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.google.common.base.Splitter;
import com.lingdonge.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 统一资源定位符相关工具类
 */
@Slf4j
public class UrlUtils {

    private UrlUtils() {
    }

    /**
     * 去掉url中的路径，留下请求参数部分，只保留？后面的结果
     *
     * @param strURL url地址
     * @return url请求参数部分
     * @author lzf
     */
    public static String getUrlQueryString(String strURL) {

        URI uri = URLUtil.toURI(strURL);
        return uri.getQuery();

//        String strAllParam = null;
//        String[] arrSplit = null;
//        strURL = strURL.trim().toLowerCase();
//        if (StringUtils.isEmpty(strURL)) {
//            return null;
//        }
//        arrSplit = strURL.split("[?]");
//        if (strURL.length() > 1) {
//            if (arrSplit.length > 1) {
//                for (int i = 1; i < arrSplit.length; i++) {
//                    strAllParam = arrSplit[i];
//                }
//            }
//        } else {
//            strAllParam = strURL;
//        }
//        return strAllParam;
    }

    /**
     * 获取URL中的Query中的某个参数，
     * 比如：url=www.ok.com/?sid=1&aid=2 可以直接获取 getUrlPara(url,"sid"); //直接取出某个参数值
     *
     * @param url
     * @param params
     * @return
     */
    public static String getUrlPara(String url, String... params) {
        String value = "";
        if (StringUtils.isBlank(url)) {
            return value;
        }
        Map<String, String> m = getUrlPara(url);
        for (String param : params) {
            try {
                String item = m.get(param);
                if (StringUtils.isEmpty(item)) {
                    continue;
                }
                value = URLDecoder.decode(item, "utf-8");
                break;
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
        }

        return value;
    }

    /**
     * 解析URL的QueryString到Map中去
     *
     * @param url
     * @return
     */
    public static Map<String, String> getUrlPara(String url) {
        Map<String, String> mapResult = new HashMap<String, String>();

        if (StringUtils.isEmpty(url)) {
            return mapResult;
        }

        String queryStr;
        if (!url.contains("?")) {
            queryStr = url;
        } else {
            queryStr = getUrlQueryString(url);
        }

        if (StringUtils.isEmpty(queryStr)) {
            return mapResult;
        }

        //每个键值为一组
//        String[] arrSplit = queryStr.split("[&]");
//        for (String strSplit : arrSplit) {
//            String[] arrSplitEqual = strSplit.split("[=]");
//
//            //解析出键值
//            if (arrSplitEqual.length > 1) {
//                //正确解析
//                mapResult.put(arrSplitEqual[0], arrSplitEqual[1]);
//
//            } else {
//                if (!"".equals(arrSplitEqual[0])) {
//                    //只有参数没有值，不加入
//                    mapResult.put(arrSplitEqual[0], "");
//                }
//            }
//        }

        mapResult = Splitter.on('&')
                .trimResults()
                .withKeyValueSeparator("=")
                .split(queryStr);
        return mapResult;

    }

    /**
     * 使用UTF-8编码URL
     *
     * @param input
     * @return
     */
    public static String encodeUtf8(String input) {
        return encode(input, "utf-8");
    }

    /**
     * 使用GBK加密URL
     *
     * @param input
     * @return
     */
    public static String encodeGbk(String input) {
        return encode(input, "gb2312");
    }

    /**
     * @param url
     * @param charset
     * @return
     */
    public static String encode(String url, String charset) {
        try {
            url = URLEncoder.encode(url, StringUtils.isNotEmpty(charset) ? charset : "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("encode trigger error ", e);
            throw new UtilException(e);
        }
        return url;
    }

    /**
     * 对URL进行解码,异常的话,会返回空字符串
     *
     * @param url
     * @return
     */
    public static String decodeUrl(String url) {
        return decode(url, "utf-8");
    }

    /**
     * URL解码，使用指定编码
     *
     * @param url
     * @param charset
     * @return
     */
    public static String decode(String url, String charset) {

        try {
            url = URLDecoder.decode(url, StringUtils.isNotEmpty(charset) ? charset : "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("decodeUrl trigger error ", e);
            throw new UtilException(e);
        }
        return url;
    }

    /**
     * 将表单数据加到URL中（用于GET表单提交）
     *
     * @param url       URL
     * @param queryPara 表单数据
     * @return 合成后的URL
     */
    public static String urlWithForm(String url, Map<String, String> queryPara) {
//        final String queryString = toParams(queryPara, CharsetUtil.UTF_8);
        final String queryString = RequestUtil.mapToQueryString(queryPara);
        return urlWithForm(url, queryString);
    }

    /**
     * 将表单数据字符串加到URL中（用于GET表单提交）
     *
     * @param url         URL
     * @param queryString 表单数据字符串
     * @return 拼接后的字符串
     */
    public static String urlWithForm(String url, String queryString) {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(url);
        if (StringUtils.isNotBlank(queryString)) {
            stringBuffer.append(url.contains("?") ? "&" : "?");
            stringBuffer.append(queryString);
        }

        return stringBuffer.toString();
    }

    /**
     * 将URL参数解析为Map（也可以解析Post中的键值对参数）
     *
     * @param paramsStr 参数字符串（或者带参数的Path）
     * @param charset   字符集
     * @return 参数Map
     */
    public static Map<String, List<String>> decodeParams(String paramsStr, String charset) {
        if (StringUtils.isBlank(paramsStr)) {
            return Collections.emptyMap();
        }

        // 去掉Path部分
        int pathEndPos = paramsStr.indexOf('?');
        if (pathEndPos > 0) {
            paramsStr = StrUtil.subSuf(paramsStr, pathEndPos + 1);
        }

        final Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        final int len = paramsStr.length();
        String name = null;
        int pos = 0; // 未处理字符开始位置
        int i; // 未处理字符结束位置
        char c; // 当前字符
        for (i = 0; i < len; i++) {
            c = paramsStr.charAt(i);
            if (c == '=' && name == null) { // 键值对的分界点
                if (pos != i) {
                    name = paramsStr.substring(pos, i);
                }
                pos = i + 1;
            } else if (c == '&' || c == ';') { // 参数对的分界点
                if (name == null && pos != i) {
                    // 对于像&a&这类无参数值的字符串，我们将name为a的值设为""
                    addParam(params, paramsStr.substring(pos, i), StringUtils.EMPTY, charset);
                } else if (name != null) {
                    addParam(params, name, paramsStr.substring(pos, i), charset);
                    name = null;
                }
                pos = i + 1;
            }
        }

        //处理结尾
        if (pos != i) {
            if (name == null) {
                addParam(params, paramsStr.substring(pos, i), StringUtils.EMPTY, charset);
            } else {
                addParam(params, name, paramsStr.substring(pos, i), charset);
            }
        } else if (name != null) {
            addParam(params, name, StringUtils.EMPTY, charset);
        }

        return params;
    }


    /**
     * 将键值对加入到值为List类型的Map中
     *
     * @param params  参数
     * @param name    key
     * @param value   value
     * @param charset 编码
     */
    private static void addParam(Map<String, List<String>> params, String name, String value, String charset) {
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<String>(1); // 一般是一个参数
            params.put(name, values);
        }
        values.add(UrlUtils.decode(value, charset));
    }

    /**
     * 根据绝对地址和相对地址，获取完整的路径
     *
     * @param absolutePath
     * @param relativePath
     * @return
     */
    public static String getAbsUrl(String absolutePath, String relativePath) {
        try {
            URL parseUrl;
            if (StringUtils.isNotEmpty(absolutePath)) {
                URL absoluteUrl = new URL(absolutePath);
                parseUrl = new URL(absoluteUrl, relativePath);
            } else {
                parseUrl = new URL(relativePath);
            }
            return parseUrl.toString();
        } catch (MalformedURLException e) {
            log.error("getAbsUrl异常：absolutePath:[{}],relativePath:[{}]发生异常", e);
            return relativePath;
        }
    }

    /**
     * canonicalizeUrl
     * <br>
     * Borrowed from Jsoup.
     *
     * @param url   url
     * @param refer refer
     * @return canonicalizeUrl
     */
    public static String canonicalizeUrl(String url, String refer) {
        URL base;
        try {
            try {
                base = new URL(refer);
            } catch (MalformedURLException e) {
                // the base is unsuitable, but the attribute may be abs on its own, so try that
                URL abs = new URL(refer);
                return abs.toExternalForm();
            }
            // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
            if (url.startsWith("?")) {
                url = base.getPath() + url;
            }
            URL abs = new URL(base, url);
            return abs.toExternalForm();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    /**
     * 检查URL是否是标准的URL格式
     *
     * @param urlString
     * @return
     */
    public static boolean isUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException mue) {
            return false;
        }
    }

    /**
     * 格式化URL链接
     *
     * @param url 需要格式化的URL
     * @return 格式化后的URL，如果提供了null或者空串，返回null
     */
    public static String formatUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        return "http://" + url;
    }


    /**
     * @param url url
     * @return new url
     * @deprecated
     */
    public static String encodeIllegalCharacterInUrl(String url) {
        return url.replace(" ", "%20");
    }

    /**
     * 删除URL中的非法字符
     *
     * @param url
     * @return
     */
    public static String fixIllegalCharacterInUrl(String url) {
        return url.replace(" ", "%20").replaceAll("#+", "#");
    }

    /**
     * @param url
     * @return
     */
    public static String getHostOld(String url) {
        String host = url;
        int i = org.apache.commons.lang3.StringUtils.ordinalIndexOf(url, "/", 3);
        if (i > 0) {
            host = org.apache.commons.lang3.StringUtils.substring(url, 0, i);
        }
        return host;
    }

    // 主机名匹配的正则
    public static final Pattern patternHost = Pattern.compile("[\\w-]+\\.(com\\.cn|net\\.cn|gov\\.cn|org\\.cn|edu\\.au|gov\\.au|com|net|org|cc|biz|info|cn|co|cc|me|tel|mobi|biz|info|name|tv|hk|uk|la|fm|jp|公司|中国|网络|university)\\b()*");

    /**
     * 获取网站的主域名
     *
     * @param url
     * @return
     */
    public static String getHost(String url) {
        url = url.toLowerCase();
        String domain = "";
        Matcher matcher = patternHost.matcher(url);
        if (matcher.find()) {
            domain = matcher.group();
        }
        if (domain == null || domain.trim().equals("")) {
            return null;
        } else {
            return domain;
        }
    }

    private static Pattern patternForProtocal = Pattern.compile("[\\w]+://");

    public static String removeProtocol(String url) {
        return patternForProtocal.matcher(url).replaceAll("");
    }

    /**
     * 获取URL中的Domain部份
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        String domain = removeProtocol(url);
        int i = org.apache.commons.lang3.StringUtils.indexOf(domain, "/", 1);
        if (i > 0) {
            domain = org.apache.commons.lang3.StringUtils.substring(domain, 0, i);
        }
        return removePort(domain);
    }


    /**
     * 删除URL中的端口部份
     *
     * @param domain
     * @return
     */
    public static String removePort(String domain) {
        int portIndex = domain.indexOf(":");
        if (portIndex != -1) {
            return domain.substring(0, portIndex);
        } else {
            return domain;
        }
    }


}
