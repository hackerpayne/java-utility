package com.lingdonge.http.httpclient;

import com.lingdonge.http.bean.HttpSetting;
import com.lingdonge.http.httpclient.interceptor.GzipInterceptor;
import com.lingdonge.http.httpclient.strategy.CustomRedirectStrategy;
import com.lingdonge.http.httpclient.strategy.MyConnectionKeepAliveStrategy;
import com.lingdonge.http.util.HttpClientCreatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CookieStore;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.Map;

/**
 * 生成HttpClient的工厂
 */
@Slf4j
public class CrawlHttpClientGenerator {
    /**
     * 连接池
     */
    private PoolingHttpClientConnectionManager connectionManager;

    /**
     * 构造函数
     * 构造时即配置好连接池的相关信息和数据
     */
    public CrawlHttpClientGenerator() {
        connectionManager = HttpClientCreatorUtil.buildPoolingConnectionManager();
    }

    /**
     * 设置连接池大小
     *
     * @param poolSize
     * @return
     */
    public CrawlHttpClientGenerator setPoolSize(int poolSize) {
        connectionManager.setMaxTotal(poolSize);
        return this;
    }

    /**
     * 获取最简单的HttpClient，不使用连接池进行管理
     *
     * @return
     */
    public CloseableHttpClient getSimpleHttpClient() {
        return HttpClients.createDefault();//如果不采用连接池就是这种方式获取连接
    }

    /**
     * 从链接池获取一个HttpClient实例，不做别的处理，仅仅是使用简单的连接池
     *
     * @return
     */
    public CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        return httpClient;
    }

    /**
     * 根据网站配置生成一个Client
     *
     * @param site
     * @return
     */
    public CloseableHttpClient getClient(HttpSetting site) {
        return generateClient(site);
    }

    /**
     * 为每个站点设置独立的Cookie存储
     *
     * @param site
     * @return
     */
    private CloseableHttpClient generateClient(HttpSetting site) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        httpClientBuilder.setConnectionManager(connectionManager);
        if (site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }
        if (site.isUseGzip()) {
            httpClientBuilder.addInterceptorFirst(new GzipInterceptor());
        }

        httpClientBuilder.setKeepAliveStrategy(new MyConnectionKeepAliveStrategy(site.getTimeOut()));

        //解决post/redirect/post 302跳转问题
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        // 设置Socket过期时间
        SocketConfig socketConfig = HttpClientCreatorUtil.buildSocketConfig(site.getTimeOut());
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultSocketConfig(socketConfig);

        // 建立Retry的机制，也可以使用上面的 buildRetryHandler
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));

        // 生成Cookie
        generateCookie(httpClientBuilder, site);

        return httpClientBuilder.build();
    }

    /**
     * 生成Cookie，根据Site里面配置的公用Cookie
     *
     * @param httpClientBuilder
     * @param site
     */
    private void generateCookie(HttpClientBuilder httpClientBuilder, HttpSetting site) {
        if (site.isDisableCookieManagement()) {
            httpClientBuilder.disableCookieManagement();
            return;
        }
        CookieStore cookieStore = new BasicCookieStore();
        for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setDomain(site.getDomain());
            cookieStore.addCookie(cookie);
        }
        for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
            for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(domainEntry.getKey());
                cookieStore.addCookie(cookie);
            }
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }

}
