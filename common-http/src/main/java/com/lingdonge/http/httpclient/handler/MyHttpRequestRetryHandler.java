package com.lingdonge.http.httpclient.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * 自建的一个Retry重试工具
 * 解决的问题：
 * httpClient默认重试策略DefaultHttpRequestRetryHandler针对连接超时和获取数据超时并不会重试，需要自定义重试策略。
 * 即：ConnectTimeoutException 不会走重试策略
 */
@Slf4j
public class MyHttpRequestRetryHandler implements HttpRequestRetryHandler {

    /**
     * 默认重试5次
     */
    private Integer maxRetryCount = 5;

    public MyHttpRequestRetryHandler() {
    }

    public MyHttpRequestRetryHandler(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public boolean retryRequest(IOException ex, int executionCount, HttpContext context) {

        // 超出最大次数，返回false
        if (executionCount > this.maxRetryCount) {
            log.info("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: 请求次数为:{} ", executionCount);
            return false;
        }
        if (ex instanceof InterruptedIOException || ex instanceof NoHttpResponseException) {
            // InterruptedIOException 超时不在处理
            // NoHttpResponseException 如果服务器丢掉了连接，那么就重试
            return true;
        }
        // Unknown host
        if (ex instanceof UnknownHostException || ex instanceof SSLException) {

            // UnknownHostException 目标服务器不可达 不在处理
            // ConnectTimeoutException 连接被拒绝 不处理
            // SSLHandshakeException SSL握手异常，不重试
            log.debug("[HttpClientUtil getCloseableHttpClient] init closeableHttpClient: 连接被拒绝 请求异常为:{} ", ex.getMessage());
            return false;
        }

        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final HttpRequest request = clientContext.getRequest();
        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
        if (idempotent) {
            // Retry if the request is considered idempotent
            // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
            return true;
        }
        return false;
    }
}
