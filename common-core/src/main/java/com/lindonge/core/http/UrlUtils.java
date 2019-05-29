package com.lindonge.core.http;

import com.lindonge.core.util.StringUtils;
import com.lindonge.core.exceptions.IORuntimeException;
import com.lindonge.core.exceptions.UtilException;
import com.lindonge.core.file.FileUtil;
import com.lindonge.core.reflect.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL 前缀表示文件: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";
    /**
     * URL 前缀表示jar: "jar:"
     */
    public static final String JAR_URL_PREFIX = "jar:";
    /**
     * URL 前缀表示war: "war:"
     */
    public static final String WAR_URL_PREFIX = "war:";
    /**
     * URL 协议表示文件: "file"
     */
    public static final String URL_PROTOCOL_FILE = "file";
    /**
     * URL 协议表示Jar文件: "jar"
     */
    public static final String URL_PROTOCOL_JAR = "jar";
    /**
     * URL 协议表示zip文件: "zip"
     */
    public static final String URL_PROTOCOL_ZIP = "zip";
    /**
     * URL 协议表示WebSphere文件: "wsjar"
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    /**
     * URL 协议表示JBoss zip文件: "vfszip"
     */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    /**
     * URL 协议表示JBoss文件: "vfsfile"
     */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    /**
     * URL 协议表示JBoss VFS资源: "vfs"
     */
    public static final String URL_PROTOCOL_VFS = "vfs";
    /**
     * Jar路径以及内部文件路径的分界符: "!/"
     */
    public static final String JAR_URL_SEPARATOR = "!/";
    /**
     * WAR路径及内部文件路径分界符
     */
    public static final String WAR_URL_SEPARATOR = "*/";

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
        String strAllParam = null;
        String[] arrSplit = null;
        strURL = strURL.trim().toLowerCase();
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                for (int i = 1; i < arrSplit.length; i++) {
                    strAllParam = arrSplit[i];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 获取URL中的Query中的某个参数，
     * 比如：url=www.ok.com/?sid=1&aid=2 可以直接获取 getUrlPara(url,"sid"); //直接取出某个参数值
     *
     * @param url
     * @param param
     * @return
     */
    public static String getUrlPara(String url, String param) {
        String value = "";
        if (org.apache.commons.lang3.StringUtils.isBlank(url)) {
            return value;
        }
        Map<String, String> m = getUrlPara(url);
        try {
            value = java.net.URLDecoder.decode(m.get(param), "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
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

        if (url == null) {
            return mapResult;
        }

        url = getUrlQueryString(url);

        //每个键值为一组
        String[] arrSplit = url.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapResult.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapResult.put(arrSplitEqual[0], "");
                }
            }
        }
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
            url = URLEncoder.encode(url, org.apache.commons.lang3.StringUtils.isNotEmpty(charset) ? charset : "UTF-8");
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
            url = URLDecoder.decode(url, org.apache.commons.lang3.StringUtils.isNotEmpty(charset) ? charset : "UTF-8");
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
            paramsStr = StringUtils.subSuf(paramsStr, pathEndPos + 1);
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
    private boolean isUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException mue) {
            return false;
        }
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     * @throws UtilException MalformedURLException
     */
    public static URL getURL(File file) {
        Assert.notNull(file, "File is null !");
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new UtilException("Error occured when get URL!", e);
        }
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     * @throws UtilException MalformedURLException
     */
    public static URL[] getURLs(File... files) {
        final URL[] urls = new URL[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw new UtilException("Error occured when get URL!", e);
        }

        return urls;
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
     * 补全相对路径
     *
     * @param baseUrl      基准URL
     * @param relativePath 相对URL
     * @return 相对路径
     * @throws UtilException MalformedURLException
     */
    public static String complateUrl(String baseUrl, String relativePath) {
        baseUrl = formatUrl(baseUrl);
        if (StringUtils.isBlank(baseUrl)) {
            return null;
        }

        try {
            final URL absoluteUrl = new URL(baseUrl);
            final URL parseUrl = new URL(absoluteUrl, relativePath);
            return parseUrl.toString();
        } catch (MalformedURLException e) {
            throw new UtilException(e);
        }
    }


    /**
     * 获得path部分
     *
     * @param uriStr URI路径
     * @return path
     * @throws UtilException 包装URISyntaxException
     */
    public static String getPath(String uriStr) {
        URI uri = null;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            throw new UtilException(e);
        }
        return uri.getPath();
    }

    /**
     * 从URL对象中获取不被编码的路径Path<br>
     * 对于本地路径，URL对象的getPath方法对于包含中文或空格时会被编码，导致本读路径读取错误。<br>
     * 此方法将URL转为URI后获取路径用于解决路径被编码的问题
     *
     * @param url {@link URL}
     * @return 路径
     * @since 3.0.8
     */
    public static String getDecodedPath(URL url) {
        String path = null;
        try {
            //URL对象的getPath方法对于包含中文或空格的问题
            path = UrlUtils.toURI(url).getPath();
        } catch (UtilException e) {
        }
        return (null != path) ? path : url.getPath();
    }

    /**
     * 转URL为URI
     *
     * @param url URL
     * @return URI
     * @throws UtilException 包装URISyntaxException
     */
    public static URI toURI(URL url) {
        if (null == url) {
            return null;
        }
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @return URI
     * @throws UtilException 包装URISyntaxException
     */
    public static URI toURI(String location) {
        try {
            return new URI(location.replace(" ", "%20"));
        } catch (URISyntaxException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 从URL中获取流
     *
     * @param url {@link URL}
     * @return InputStream流
     * @since 3.2.1
     */
    public static InputStream getStream(URL url) {
        Assert.notNull(url);
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 提供的URL是否为文件<br>
     * 文件协议包括"file", "vfsfile" 或 "vfs".
     *
     * @param url {@link URL}
     * @return 是否为文件
     * @since 3.0.9
     */
    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || //
                URL_PROTOCOL_VFSFILE.equals(protocol) || //
                URL_PROTOCOL_VFS.equals(protocol));
    }

    /**
     * 提供的URL是否为jar包URL
     * 协议包括： "jar", "zip", "vfszip" 或 "wsjar".
     *
     * @param url {@link URL}
     * @return 是否为jar包URL
     */
    public static boolean isJarURL(URL url) {
        final String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) || //
                URL_PROTOCOL_ZIP.equals(protocol) || //
                URL_PROTOCOL_VFSZIP.equals(protocol) || //
                URL_PROTOCOL_WSJAR.equals(protocol));
    }

    /**
     * 提供的URL是否为Jar文件URL
     * 判断依据为file协议且扩展名为.jar
     *
     * @param url the URL to check
     * @return whether the URL has been identified as a JAR file URL
     * @since 4.1
     */
    public static boolean isJarFileURL(URL url) {
        return (URL_PROTOCOL_FILE.equals(url.getProtocol()) && //
                url.getPath().toLowerCase().endsWith(FileUtil.JAR_FILE_EXT));
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
