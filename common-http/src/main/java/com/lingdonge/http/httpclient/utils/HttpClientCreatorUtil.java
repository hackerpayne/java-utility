package com.lingdonge.http.httpclient.utils;

import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.http.httpclient.strategy.CustomRedirectStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.List;

/**
 * 创建不同的HttpClient对象
 */
@Slf4j
public class HttpClientCreatorUtil {

    /**
     * 初始化一个RequestConfig对象， 用于使用代理
     *
     * @param proxyModel
     * @return
     */
    public static RequestConfig buildRequestConfig(ModelProxy proxyModel) {
        RequestConfig config = null;
        //使用代理
        if (null != proxyModel && StringUtils.isNotBlank(proxyModel.getHost())) {
            HttpHost proxy = new HttpHost(proxyModel.getHost(), proxyModel.getPort());
            config = RequestConfig.custom().setProxy(proxy).build();
        } else {
            //没有代理，使用默认值
            config = RequestConfig.custom().build();
        }
        return config;
    }

    /**
     * 创建默认的PoolingHttpClientConnectionManager对象
     *
     * @return
     */
    public static PoolingHttpClientConnectionManager buildPoolingConnectionManager() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", HttpClientSslUtil.buildSSLConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(reg);

        //  1、MaxtTotal是整个池子的大小；
        //  2、DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
        //  MaxtTotal=400 DefaultMaxPerRoute=200
        //  而我只连接到http://sishuok.com时，到这个主机的并发最多只有200；而不是400；
        //  而我连接到http://sishuok.com 和 http://qq.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute。
        connectionManager.setMaxTotal(500); // 设置整个连接池最大连接数 根据自己的场景决定 该值就是连接不够用的时候等待超时时间，一定要设置，而且不能太大
        connectionManager.setDefaultMaxPerRoute(100); // 每台主机分配的连接数字，是路由的默认最大连接（该值默认为2），限制数量实际使用DefaultMaxPerRoute并非MaxTotal。
        connectionManager.setDefaultSocketConfig(buildSocketConfig(2000));

        return connectionManager;
    }

    /**
     * 创建SocketConfig，主要是设置过期时间，使用时：
     * connectionManager
     *
     * @param timeOut
     * @return
     */
    public static SocketConfig buildSocketConfig(Integer timeOut) {
        SocketConfig.Builder socketConfigBuilder = SocketConfig.custom();
        socketConfigBuilder.setSoKeepAlive(true).setTcpNoDelay(true);
        socketConfigBuilder.setSoTimeout(timeOut);
        SocketConfig socketConfig = socketConfigBuilder.build();
        return socketConfig;
    }

    /**
     * 添加Cookie到CookieStore
     * 使用时：httpClientBuilder.setDefaultCookieStore(cookieStore);
     *
     * @param listCookies
     * @return
     */
    public static CookieStore buildCookie(List<Cookie> listCookies) {
        CookieStore cookieStore = new BasicCookieStore();

        listCookies.forEach(item -> {
            BasicClientCookie cookie = new BasicClientCookie(item.getName(), item.getValue());
            if (StringUtils.isNotEmpty(item.getDomain())) {
                cookie.setDomain(item.getDomain());
            }
            cookieStore.addCookie(cookie);
        });

        return cookieStore;
    }

    /**
     * Https请求对象，信任所有证书
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getSslSafeCloseableClient() {
        return HttpClients.custom()
                .setSSLSocketFactory(HttpClientSslUtil.buildSSLConnectionSocketFactory())
                .setConnectionManager(buildPoolingConnectionManager())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)) // 设置重试次数，一般不要
                .setDefaultRequestConfig(RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()) // setStaleConnectionCheckEnabled方法来逐出已被关闭的链接不被推荐
                .build();
    }


    /**
     * 使用HttpClientBuilder创建的Client，可以做多种自定义操作，包括添加代理和Cookie等
     *
     * @return
     */
    public static CloseableHttpClient buildHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        httpClientBuilder.setConnectionManager(buildPoolingConnectionManager());

//        httpClientBuilder.setDefaultCookieStore();// 指定Cookie进行请求

//        httpClientBuilder.setUserAgent("");// 设置UA
//        httpClientBuilder.addInterceptorFirst(new GzipInterceptor()); // 允许Gzip压缩
//        httpClientBuilder.setKeepAliveStrategy(new MyConnectionKeepAliveStrategy(site.getTimeOut()));

        //解决post/redirect/post 302跳转问题
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        // 设置Socket过期时间
//        SocketConfig socketConfig = HttpClientCreatorUtil.buildSocketConfig(site.getTimeOut());
//        httpClientBuilder.setDefaultSocketConfig(socketConfig);
//        connectionManager.setDefaultSocketConfig(socketConfig);
//
        // 建立Retry的机制，也可以使用上面的 buildRetryHandler
//        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));

        return httpClientBuilder.build();
    }
}
