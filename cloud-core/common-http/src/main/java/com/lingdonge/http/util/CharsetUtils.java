package com.lingdonge.http.util;

import cn.hutool.core.util.ReUtil;
import com.lingdonge.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 编码识别处理类
 */
@Slf4j
public class CharsetUtils {

    public static final Pattern CHARSET_PATTERN = Pattern.compile("charset=(.*?)\"");

    private static final Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)", Pattern.CASE_INSENSITIVE);

    /**
     * 从Http连接的头信息中获得字符集<br>
     * 从ContentType中获取
     *
     * @param conn HTTP连接对象
     * @return 字符集
     */
    public static String getCharset(HttpURLConnection conn) {
        if (conn == null) {
            return null;
        }

        return ReUtil.get(CHARSET_PATTERN, conn.getContentType(), 1);
    }

    /**
     * 获取页面编码
     *
     * @param contentType
     * @return
     */
    public static String getCharset(String contentType) {
        Matcher matcher = patternForCharset.matcher(contentType);
        if (matcher.find()) {
            String charset = matcher.group(1);
            if (Charset.isSupported(charset)) {
                return charset;
            }
        }
        return null;
    }

    /**
     * 通过Jsoup对内容的头部进行字符串匹配识别
     *
     * @param contentType
     * @param contentBytes
     * @return
     * @throws IOException
     */
    public static String detectCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset;
        // charset
        // 1、encoding in http header Content-Type
        charset = getCharset(contentType);
        if (StringUtils.isNotBlank(contentType) && StringUtils.isNotBlank(charset)) {
            log.debug("Auto get charset: {}", charset);
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset);
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        log.debug("Auto get charset: {}", charset);
        // 3、todo use tools as cpdetector for content decode
        return charset;
    }

}
