package com.lingdonge.http.jsouputil;

import com.lindonge.core.http.HttpClientUtils;
import com.lindonge.core.http.UserAgentUtil;
import com.lindonge.core.threads.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * 使用Jsoup做HTTP请求
 */
@Slf4j
public class JsoupHttpUtil {

    /**
     * 使用默认UA及3次重试
     *
     * @param url
     * @return
     */
    public static Document getHtml(String url) {
        return getHtml(url, "", 3);
    }

    /**
     * 使用指定UA获取HTML
     *
     * @param url
     * @param ua
     * @return
     */
    public static Document getHtml(String url, String ua) {
        Document doc = null;
        try {
            HttpClientUtils.trustEveryone();
            doc = Jsoup.connect(url).userAgent(ua).timeout(10000).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }


    /**
     * 获取HTML结果
     *
     * @param url
     * @param ua
     * @param maxTry
     * @return
     */
    public static Document getHtml(String url, String ua, Integer maxTry) {
        Document doc = null;
        int i = 0;

        while (i < 3) {
            try {
                doc = Jsoup.connect(url)
                        .userAgent(StringUtils.isNotEmpty(ua) ? ua : UserAgentUtil.UAPc) //有UA设置，没有使用默认
                        .ignoreHttpErrors(true).timeout(20000)
                        .followRedirects(true).execute().parse();
                break;
            } catch (Exception e) {
                i++;
                ThreadUtil.sleep(1000);
            }
        }
        if (i >= maxTry || null == doc) {
            return null;
        }

        return doc;
    }

    public static String getHtmlStr(String url) throws IOException {
        //获取 Cookie
        Connection.Response res = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute();
//        String sessionId = res.cookie(sessionName);
//        System.err.println(sessionId);

        //登录请求提交
        Connection.Response login = Jsoup.connect("loginAction.do")
//                .header("Cookie", sessionName + "=" + sessionId)  //携带刚才的 Cookie 信息
                .data("zjh", "账号", "mm", "密码")
                //这里的 zjh 和 mm 就是登录页面 form 表单的 name
                .method(Connection.Method.POST)
                .execute();

        //此时 sessionId 为可用状态
        Document scoreDoc = Jsoup.connect(url)
//                .cookie(sessionName, sessionId)
                .get();
        System.err.println(scoreDoc);

        return scoreDoc.body().html();
    }

    /**
     * 使用默认信息采集数据
     *
     * @param url
     * @return
     */
    public static Connection.Response getResponse(String url) {
        return getResponse(url, "", 3);
    }

    /**
     * 获取Response信息
     *
     * @param url
     * @param ua
     * @param maxTry
     * @return
     */
    public static Connection.Response getResponse(String url, String ua, Integer maxTry) {
        Connection.Response response = null;
        int i = 0;

        while (i < 3) {
            try {
                response = Jsoup.connect(url)
                        .userAgent(StringUtils.isNotEmpty(ua) ? ua : UserAgentUtil.UAPc) //有UA设置，没有使用默认
                        .ignoreHttpErrors(true).timeout(20000)
                        .followRedirects(true).execute();
                break;
            } catch (Exception e) {
                i++;
                ThreadUtil.sleep(1000);
            }
        }
        if (i >= maxTry || null == response) {
            return null;
        }

        return response;
    }
}
