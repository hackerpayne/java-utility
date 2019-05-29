package com.lindonge.core.http;

import cn.hutool.core.io.IoUtil;
import com.lindonge.core.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kyle on 16/6/15.
 */
@Slf4j
@Getter
@Setter
public class HttpHelper {

    private CloseableHttpClient httpClient = null;
    private String userAgent = "";
    private String location = "";
    private CookieStore cookieStore = null;

    /**
     * 构造函数
     */
    public HttpHelper() {
        cookieStore = new BasicCookieStore();
//        this.httpClient = HttpClients.createDefault();

        // 定义全局配置文件，可以在Get里面用get.setConfig(configuration);配置，或者直接用全局的
        RequestConfig globalConfig = RequestConfig.custom()
                .setConnectTimeout(6000)
                .setSocketTimeout(6000)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .build();

        StandardHttpRequestRetryHandler standardHandler = new StandardHttpRequestRetryHandler(5, true);

        // 设置重试规则
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {

            @Override
            public boolean retryRequest(IOException arg0, int retryTimes, HttpContext arg2) {
                if (arg0 instanceof UnknownHostException || arg0 instanceof ConnectTimeoutException
                        || !(arg0 instanceof SSLException) || arg0 instanceof NoHttpResponseException) {
                    return true;
                }
                if (retryTimes > 5) {
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(arg2);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
                    return true;
                }
                return false;
            }
        };

//        HttpHost proxy = new HttpHost("127.0.0.1", 80);// 设置代理ip
//        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

        this.httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(globalConfig)
                .setRetryHandler(handler) //设置重试规则
//                .setRoutePlanner(routePlanner) //设置代理IP
                .build();

    }

    public static void main(String[] args) throws IOException {

        System.out.println("multithreading http");

        HttpHelper http = new HttpHelper();

        String html = http.getHtml("http://www.baidu.com", "", "", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36", false);

        String cookie = http.getCookieStr();
        System.out.println("当前Cookie1为：" + cookie);
//        System.out.println(html);

        html = http.getHtml("http://baike.baidu.com", "", "", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36", false);
        cookie = http.getCookieStr();
        System.out.println("当前Cookie2为：" + cookie);
//        System.out.println(html);


//
//        //创建一个客户端
//        CloseableHttpClient client = HttpClients.createDefault();
//
//        //创建一个get方法
//        HttpGet get = new HttpGet("http://www.baidu.com");
//
//        //执行请求
//        HttpResponse res = client.execute(get);
//
//        //获取协议版本・・・「HTTP/1.1」
//        System.out.println(res.getProtocolVersion());
//        //获取返回状态码・・・「200」
//        System.out.println(res.getStatusLine().getStatusCode());
//        //获取原因短语・・・「OK」
//        System.out.println(res.getStatusLine().getReasonPhrase());
//        //获取完整的StatusLine・・・「HTTP/1.1 200 OK」
//        System.out.println(res.getStatusLine().toString());
//
//        //获取返回头部信息
//        Header[] headers = res.getAllHeaders();
//        for (Header header : headers) {
//            System.out.println(header.getName() + ": " + header.getValue());
//        }
//
//        //获取返回内容
//        if (res.getEntity() != null) {
//            System.out.println(EntityUtils.toString(res.getEntity()));
//        }
//
//        //关闭流
//        EntityUtils.consume(res.getEntity());
//
//        //关闭连接
//        client.close();
    }


    /**
     * 获取Cookie字符串列表
     *
     * @return
     */
    public String getCookieStr() {
        String cookie = "";
        if (cookieStore != null) {
            List<Cookie> listCookies = cookieStore.getCookies();
            StringBuilder sb = new StringBuilder();
            if (listCookies != null && listCookies.size() > 0) {
                for (Cookie ck : listCookies) {
                    sb.append(ck.getName() + "=" + ck.getValue() + ";");
                }
                cookie = sb.toString();
            }
        }

        return cookie;
    }

    /**
     * 从Response中读取Cookie
     *
     * @param response
     * @return
     */
    public String getCookiesFromResponse(HttpResponse response) {
        Header[] headers = response.getHeaders("Set-Cookie");
        StringBuilder sb = new StringBuilder();
        for (Header header : headers) {
            sb.append(header.getValue()).append(";");
        }
        return sb.toString();
    }

    /**
     * 获取Cookie列表
     *
     * @return
     */
    public List<Cookie> getCookieList() {
        return cookieStore.getCookies();
    }

    /**
     * 清空Cookie
     */
    public void clearCookie() {
        if (cookieStore != null) {
            cookieStore.clear();
        }
    }

    /**
     * 添加一个Cookie进来
     *
     * @param ck
     */
    public void addCookie(Cookie ck) {
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
        }

        if (ck != null) {
            cookieStore.addCookie(ck);
        }
    }

    /**
     * 打印Header信息
     *
     * @param response
     */
    public static void printAllHeader(HttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            log.info(header.getName() + " " + header.getValue());
        }
    }

    /**
     * 获取HTML
     *
     * @param url
     * @return
     */
    public String getHtml(String url) {
        return getHtml(url, "", "", this.getUserAgent(), false);
    }

    /**
     * 使用指定的UA进行访问
     *
     * @param url
     * @param referer
     * @return
     */
    public String getHtml(String url, String referer) {
        return getHtml(url, "", referer, this.getUserAgent(), false);
    }

    /**
     * 获取HTML
     *
     * @param url
     * @param cookie
     * @return
     */
    public String getHtml(String url, String cookie, String referer, String userAgent, boolean boolKeepAlive) {
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        String html = "";

        try {
            if (StringUtils.isNotEmpty(cookie)) {
                httpGet.setHeader("Set-Cookie", cookie);
            }

            if (StringUtils.isNotEmpty(userAgent)) {
                httpGet.setHeader("User-Agent", userAgent);
            }

            if (StringUtils.isNotEmpty(referer)) {
                httpGet.setHeader("Referer", referer);
            }

            if (boolKeepAlive) {
                httpGet.setHeader("Connection", "keep-alive");
            }

            response = this.httpClient.execute(httpGet);

            Header head = response.getFirstHeader("Location");
            if (head != null) {
                String location = head.getValue();
                this.setLocation(location);
            }

            html = responseToStr(response);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            IoUtil.close(response);
            return html;
        }
    }


    /**
     * 上传文件
     *
     * @param postUrl  上传URL
     * @param postFile 上传文件本地
     */
    public String upload(String postUrl, String postFile) {

        String html = "";

        CloseableHttpResponse response = null;

        try {
//            HttpPost httpPost = new HttpPost(postUrl);
//
//            FileBody bin = new FileBody(new File(postFile));
//            StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
//
//            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("bin", bin).addPart("comment", comment).build();
//
//            httpPost.setEntity(reqEntity);
//
//            response = this.httpClient.execute(httpPost);
//
//            html = responseToStr(response);

        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("", e);
            }
            return html;
        }
    }

    /**
     * 使用Cookie下载图片
     *
     * @param url
     * @param cookie
     * @return
     * @throws IOException
     */
    public BufferedImage getImage(String url, String cookie) {

        HttpGet httpGet = new HttpGet(url);

        if (StringUtils.isNotEmpty(cookie)) {
            httpGet.setHeader("Set-Cookie", cookie);
        }

        CloseableHttpResponse response = null;
        try {
            response = this.httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            ByteArrayInputStream is = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
            BufferedImage img = ImageIO.read(is);
            is.close();
            return img;

        } catch (IOException e) {
            log.error("", e);
            return null;
        }
    }

    /**
     * post方式提交表单（模拟用户登录请求）
     *
     * @param url
     * @param postData
     * @return
     */
    public String postHtml(String url, HashMap<String, String> postData) {
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        for (Map.Entry<String, String> item : postData.entrySet()) {
            formparams.add(new BasicNameValuePair(item.getKey(), item.getValue()));
        }

        UrlEncodedFormEntity uefEntity = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            return postHtml(url, uefEntity, "", this.getUserAgent(), "");

        } catch (Exception e) {
            log.error("", e);
            return "";
        }


    }

    /**
     * postHtml发送
     *
     * @param url
     * @return
     * @throws IOException
     */
    public String postHtml(String url, List<NameValuePair> postData) {

        try {
            return postHtml(url, new UrlEncodedFormEntity(postData), "", this.getUserAgent(), "");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
            return "";
        }
    }

    /**
     * @param url
     * @param postData
     * @return
     */
    public String postJson(String url, String postData) {
        return postJson(url, postData, "", this.getUserAgent(), "");
    }

    /**
     * 提交Json格式的数据
     *
     * @param url
     * @param postData
     * @return
     */
    public String postJson(String url, String postData, String cookie, String userAgent, String referer) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");

        String html = "";

        CloseableHttpResponse response = null;
        try {

            if (StringUtils.isNotEmpty(cookie)) {
                httpPost.setHeader("Set-Cookie", cookie);
            }

            if (StringUtils.isNotEmpty(userAgent)) {
                httpPost.setHeader("User-Agent", userAgent);
            }

            if (StringUtils.isNotEmpty(referer)) {
                httpPost.setHeader("Referer", referer);
            }

            if (StringUtils.isNotEmpty(postData)) {
                StringEntity se = new StringEntity(postData);
                se.setContentType("text/json");
                httpPost.setEntity(se);
            }

            response = this.httpClient.execute(httpPost);
            html = responseToStr(response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return html;
    }


    /**
     * post方式提交表单（模拟用户登录请求）
     *
     * @param url
     * @param postEntity
     * @param cookie
     * @param userAgent
     * @param referer
     * @return
     */
    public String postHtml(String url, HttpEntity postEntity, String cookie, String userAgent, String referer) {

        CloseableHttpResponse response = null;
        String html = "";

        // 创建HttpPost
        HttpPost httpPost = new HttpPost(url);

        try {
            if (postEntity != null) {
                httpPost.setEntity(postEntity);
            }

            if (StringUtils.isNotEmpty(cookie)) {
                httpPost.setHeader("Set-Cookie", cookie);
            }

            if (StringUtils.isNotEmpty(userAgent)) {
                httpPost.setHeader("User-Agent", userAgent);
            }

            if (StringUtils.isNotEmpty(referer)) {
                httpPost.setHeader("Referer", referer);
            }

            response = this.httpClient.execute(httpPost);

            html = responseToStr(response);

        } catch (Exception e) {
            log.error("", e);
        } finally {
            IoUtil.close(response);
            return html;
        }

    }


    /**
     * Response中提取返回结果
     *
     * @param response
     * @return
     * @throws IOException
     */
    public String responseToStr(CloseableHttpResponse response) throws IOException {
        String html = "";

        try {
//            System.out.println(response.getStatusLine());

//            HttpEntity entity = response.getEntity();

            // do something useful with the response body
            // and ensure it is fully consumed
            //EntityUtils.consume(entity);

            html = EntityUtils.toString(response.getEntity());
        } finally {
            response.close();
        }
        return html;
    }

    /**
     * 关闭HttpClient
     */
    public void close() {
        if (this.httpClient != null) {
            try {
                this.httpClient.close();
            } catch (IOException e) {
                log.error("关闭HttpClient出错", e);
            }
        }
    }

}
