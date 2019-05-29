package com.lingdonge.http.httpclient;

import com.lindonge.core.model.ModelProxy;
import com.lindonge.core.util.StringUtils;
import com.lingdonge.http.httpclient.utils.HttpClientCreatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.Map;

/**
 * 简化版本的HttpClient请求类
 */
@Slf4j
public class HttpClientSimpleHelper {

    private static Integer connectTimeout = 10 * 1000;

    private static Integer socketTimeout = 30 * 1000;

    private static final Integer DEFAULT_MAX_TOTAL = 200;
    private static final Integer DEFAULT_MAX_PER_ROUTE = 20;

    private PoolingHttpClientConnectionManager cm;

    private Registry<ConnectionSocketFactory> registry;

    /**
     * 初始化
     */
    private void init() {
        try {
            SSLContextBuilder builder = SSLContexts.custom();
            builder.loadTrustMaterial(null, (chain, authType) -> true);
            SSLContext sslContext = builder.build();
            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(
                    sslContext, new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "SSLv3"}, null, NoopHostnameVerifier.INSTANCE);

            registry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslSF)
                    .build();

            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(DEFAULT_MAX_TOTAL);
            cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

        } catch (Exception ex) {
            log.error("Can't initialize connection manager! Exiting");
        }
    }

    /**
     * 创建一个Connection
     *
     * @return
     */
    public CloseableHttpClient getConnection() {
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 获取HTML结果
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getHtmlSimple(String url) throws IOException {
        HttpResponse httpResponse = Request.Get(url)
                .connectTimeout(connectTimeout)
                .socketTimeout(socketTimeout)
                .execute()
                .returnResponse();
        return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
    }

    /**
     * 发送GET请求
     *
     * @param url 请求地址
     * @return 响应结果
     */
    public static String getHtml(String url) {

        CloseableHttpClient httpClient = null;
        try {
            if (StringUtils.contains(url.toLowerCase(), "https")) {
                httpClient = HttpClientCreatorUtil.getSslSafeCloseableClient();
            } else {
                httpClient = HttpClients.createDefault();
            }

            HttpGet httpget = new HttpGet(url);
            // HttpGet设置请求头的两种种方式
            // httpget.addHeader(new BasicHeader("Connection", "Keep-Alive"));
            httpget.addHeader("Connection", "Keep-Alive");
            httpget.addHeader("Accept-Encoding", "gzip, deflate");
//            Header[] heads = httpget.getAllHeaders();
//            for (int i = 0; i < heads.length; i++) {
//                System.out.println(heads[i].getName() + "-->" + heads[i].getValue());
//            }
            CloseableHttpResponse response = httpClient.execute(httpget);
            return HttpClientUtil.handleResponseToStr(response, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("httpClient.close()异常");
            }
        }
    }

    /**
     * 发送POST请求
     *
     * @param url     请求地址
     * @param data    请求实体内容
     * @param useJson 使用Post时使用Json请求
     * @return 响应结果
     */
    public static String postHtml(String url, String data, Boolean useJson) {
        CloseableHttpClient httpClient = null;
        try {
            if (StringUtils.contains(url.toLowerCase(), "https")) {
                httpClient = HttpClientCreatorUtil.getSslSafeCloseableClient();
            } else {
                httpClient = HttpClients.createDefault();
            }
            HttpPost httpPost = new HttpPost(url);

            if (useJson) {
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            }

            // HttpPost设置请求头的两种种方式
            // httpPost.addHeader(new BasicHeader("Connection", "Keep-Alive"));
            // httpPost.addHeader("Connection", "Keep-Alive");
            // UrlEncodedFormEntity处理键值对格式请求参数
            // List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            // new UrlEncodedFormEntity(list, "UTF-8");

            if (null != data) {
                if (useJson) {
                    httpPost.setEntity(HttpClientUtil.buildStringEntity(data, true));
                } else {
                    httpPost.setEntity(HttpClientUtil.buildStringEntity(data, false));
                }
            }

            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
            httpPost.setConfig(requestConfig);

            HttpResponse response = httpClient.execute(httpPost);
            return HttpClientUtil.handleResponseToStr(response, Charset.forName("utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getCause().getMessage());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            log.error("连接超时：" + url);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("IO异常:" + url);
        } finally {
            try {
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("httpClient.close()异常");
            }
        }
        return null;
    }


    /**
     * post请求(用于key-value格式的参数)
     *
     * @param url
     * @param params
     * @return
     */
    public static String postMap(String url, Map params) {

        BufferedReader in = null;
        try {
            // 定义HttpClient
            HttpClient client = HttpClients.createDefault();
            // 实例化HTTP方法
            HttpPost request = new HttpPost(new URI(url));

            request.setEntity(HttpClientUtil.buildUrlEncodeEntity(params));

            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (200 == code) {
                return HttpClientUtil.handleResponseToStr(response, Charset.forName("utf-8"));
            } else {   //
                log.info("状态码：" + code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    /**
     * Http简单请求
     *
     * @param url
     * @param jsonStr
     * @param modelProxy
     * @return
     * @throws UnsupportedEncodingException
     */
    public HttpPost postJson(String url, String jsonStr, ModelProxy modelProxy) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(jsonStr)) {
            throw new InvalidParameterException("Invalid url or requestJson");
        }

        HttpPost request = new HttpPost(url);
        StringEntity se = new StringEntity(jsonStr);
        request.setEntity(se);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        if (StringUtils.isNotBlank(modelProxy.getHost())) {
            HttpHost proxy = new HttpHost(modelProxy.getHost(), modelProxy.getPort()); // 代理主机地址与端口
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            request.setConfig(config);
        }
        return request;
    }

    /**
     * 下载文件
     *
     * @param url
     * @param savePath
     * @param proxyModel
     */
    public void downloadFile(String url, String savePath, ModelProxy proxyModel) {

        CloseableHttpClient client = HttpClients.createDefault();

        RequestConfig config = HttpClientCreatorUtil.buildRequestConfig(proxyModel);

        //目标文件url
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        //下载需登陆，设置登陆后的cookie
        //  httpGet.addHeader("Cookie", cookie);

        try {
            HttpResponse respone = client.execute(httpGet);
            if (respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return;
            }
            HttpClientUtil.handleResponseToFile(respone, savePath);
        } catch (Exception e) {

        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
