package com.lingdonge.spider.webmagic.downloader;

import com.lingdonge.spider.webmagic.Site;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * 生成HttpClient的工厂
 */
public class HttpClientGenerator {

    private transient Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 连接池
     */
    private PoolingHttpClientConnectionManager connectionManager;

    /**
     * 构造函数
     */
    public HttpClientGenerator() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", buildSSLConnectionSocketFactory())
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setMaxTotal(500); // 该值就是连接不够用的时候等待超时时间，一定要设置，而且不能太大
        connectionManager.setDefaultMaxPerRoute(100); // 每台主机分配的连接数字

    }

    /**
     * @return
     */
    private SSLConnectionSocketFactory buildSSLConnectionSocketFactory() {
        try {
//            return new SSLConnectionSocketFactory(createIgnoreVerifySSL()); // 优先绕过安全证书

            // 支持TLS 1.2
            return new SSLConnectionSocketFactory(createIgnoreVerifySSL(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"},
                    null,
                    new DefaultHostnameVerifier()); // 优先绕过安全证书

        } catch (KeyManagementException e) {
            logger.error("ssl connection fail", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("ssl connection fail", e);
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    /**
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        SSLContext sc = SSLContext.getInstance("SSLv3");
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 设置连接池大小
     *
     * @param poolSize
     * @return
     */
    public HttpClientGenerator setPoolSize(int poolSize) {
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
     * 暂未使用，自建的一个Retry重试工具
     *
     * @param maxRetryCount
     * @return
     */
    private HttpRequestRetryHandler buildRetryHandler(Integer maxRetryCount) {
        // 设置重试规则
//        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
//
//            @Override
//            public boolean retryRequest(IOException arg0, int retryTimes, HttpContext arg2) {
//                if (arg0 instanceof UnknownHostException || arg0 instanceof ConnectTimeoutException
//                        || !(arg0 instanceof SSLException) || arg0 instanceof NoHttpResponseException) {
//                    return true;
//                }
//                if (retryTimes > 5) {
//                    return false;
//                }
//                HttpClientContext clientContext = HttpClientContext.adapt(arg2);
//                HttpRequest request = clientContext.getRequest();
//                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
//                if (idempotent) {
//                    // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
//                    return true;
//                }
//                return false;
//            }
//        };

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                // 如果达到了请求错误次数  就不在请求
                if (i >= maxRetryCount) {
                    logger.info("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: 请求次数为:{} ", i);
                    return false;
                }
                // 处理异常
                if (e instanceof NoHttpResponseException) {
                    // 如果服务器丢掉了连接，那么就重试
                    logger.error("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: 服务器丢掉了连接 请求异常为:{} ", e.getMessage());
                    return true;
                }
                if (e instanceof SSLHandshakeException || e instanceof SSLException) {
                    // 不要重试SSL握手异常
                    logger.error("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: SSL异常 请求异常为:{} ", e.getMessage());
                    return false;
                }
                if (e instanceof InterruptedIOException) {
                    // 超时不在处理
                    logger.error("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: 超时 请求异常为:{} ", e.getMessage());
                    return false;
                }
                if (e instanceof UnknownHostException) {
                    // 目标服务器不可达 不在处理
                    logger.error("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: 目标服务器不可达 请求异常为:{} ", e.getMessage());
                    return false;
                }
                if (e instanceof ConnectTimeoutException) {
                    // 连接被拒绝 不在处理
                    logger.error("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: 连接被拒绝 请求异常为:{} ", e.getMessage());
                    return false;
                }
                HttpClientContext httpClientContext = HttpClientContext.adapt(httpContext);
                HttpRequest request = httpClientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        return httpRequestRetryHandler;
    }

    /**
     * @param site
     * @return
     */
    public CloseableHttpClient getClient(Site site) {
        return generateClient(site);
    }

    /**
     * 为每个站点设置独立的Cookie存储
     *
     * @param site
     * @return
     */
    private CloseableHttpClient generateClient(Site site) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        httpClientBuilder.setConnectionManager(connectionManager);
        if (site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }
        if (site.isUseGzip()) {
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {

                public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });
        }

        // 解决豆瓣http采集时的连接池使用问题，参考：https://www.cnblogs.com/aisam/p/7652680.html
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(org.apache.http.HttpResponse httpResponse,
                                             HttpContext httpContext) {
                return site.getTimeOut(); // 20 seconds,because tomcat default keep-alive timeout is 20s
            }
        };
        httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy);

        //解决post/redirect/post 302跳转问题
        httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(true)
                .setTcpNoDelay(true)
                .setSoTimeout(site.getTimeOut())
                .setSoLinger(-1) //参考VSCrawler
                .setSoReuseAddress(false) //参考VSCrawler
                .build();

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
    private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
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
