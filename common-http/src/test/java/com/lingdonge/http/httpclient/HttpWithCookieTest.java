package com.lingdonge.http.httpclient;

import com.lingdonge.http.httpclient.utils.HttpClientCreatorUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 使用Cookie有2种方法
 * 1、通过HttpClientContext 传递cookie
 * 2、使用CookieStore
 */
public class HttpWithCookieTest {

    /**
     * 使用 HttpClientContext 做Cookie操作
     *
     * @return
     */
    public static String testWithCookie1() {

        CloseableHttpClient closeableHttpClient = HttpClientCreatorUtil.buildHttpClient();

        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie basicClientCookie = new BasicClientCookie("key", "value");
        basicClientCookie.setDomain("baidu.com");
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);

        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setCookieStore(cookieStore);

        HttpGet httpGet = new HttpGet("www.baidu.com");
        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet, httpClientContext);// 传递Cookie
            return HttpResultUtil.responseToStr(httpResponse, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String testWithCookie2() {

        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie basicClientCookie = new BasicClientCookie("key", "value");
        basicClientCookie.setDomain("baidu.com");
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);

        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore) // 设置Cookie
                .build();

        HttpGet httpGet = new HttpGet("www.baidu.com");
        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);// 传递Cookie
            return HttpResultUtil.responseToStr(httpResponse, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
