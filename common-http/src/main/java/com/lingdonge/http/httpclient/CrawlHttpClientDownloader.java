package com.lingdonge.http.httpclient;

import com.lingdonge.core.http.HttpClientUtils;
import com.lingdonge.core.http.HttpConstant;
import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.http.HttpRequest;
import com.lingdonge.http.HttpResult;
import com.lingdonge.http.HttpSetting;
import com.lingdonge.http.proxy.ProxyProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 从WebMagic里面抽出来的代码，专门进行数据抓取
 */
@Slf4j
public class CrawlHttpClientDownloader {

    private final Map<String, CloseableHttpClient> httpClients = new HashMap();

    private CrawlHttpClientGenerator httpClientGenerator = new CrawlHttpClientGenerator();

    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();

    private ProxyProvider proxyProvider;

    public void setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    public CrawlHttpClientDownloader() {
    }

    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }

    /**
     * 根据Site从池里获取新的HttpClient
     *
     * @param site
     * @return
     */
    private CloseableHttpClient getHttpClient(HttpSetting site) {
        if (site == null) {
            return this.httpClientGenerator.getClient((HttpSetting) null);
        } else {
            String domain = site.getDomain();
            CloseableHttpClient httpClient = (CloseableHttpClient) this.httpClients.get(domain);
            if (httpClient == null) {
                synchronized (this) {
                    httpClient = (CloseableHttpClient) this.httpClients.get(domain);
                    if (httpClient == null) {
                        httpClient = this.httpClientGenerator.getClient(site);
                        this.httpClients.put(domain, httpClient);
                    }
                }
            }

            return httpClient;
        }
    }

    /**
     * 下载页面请求
     *
     * @param request
     * @param httpSetting
     * @return
     */
    public HttpResult download(HttpRequest request, HttpSetting httpSetting) {

        if (httpSetting == null) {
            throw new NullPointerException("task or site can not be null");
        }

        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = this.getHttpClient(httpSetting);
        ModelProxy proxy = this.proxyProvider != null ? this.proxyProvider.getProxy(httpSetting) : null;
        HttpClientRequestContext requestContext = this.httpUriRequestConverter.convert(request, httpSetting, proxy);
        HttpResult page = new HttpResult();

        HttpResult var9;
        try {

            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());

            List<URI> locationList = requestContext.getHttpClientContext().getRedirectLocations();

            if (locationList != null && locationList.size() > 0) {
                page.setLocationUrl(locationList.get(locationList.size() - 1).toString());
            }

            // 返回码大于400的，都是错误 ，需要重试请求
            if (httpResponse.getStatusLine().getStatusCode() > 400) {
                log.warn("download page {} error,Status could not be {}", request.getUrl(), httpResponse.getStatusLine().getStatusCode());
                page.setDownloadSuccess(false);
                return page;
            }

            page = HttpClientUtil.handleResponse(request, request.getCharset() != null ? request.getCharset() : httpSetting.getCharset(), httpResponse);

            log.info("downloading page success {}", request.getUrl());
            HttpResult var8 = page;
            return var8;
        } catch (IOException var13) {

            // 代理里面出现SocketTimeOut太多了，屏蔽掉异常说明
            if (var13 instanceof SocketTimeoutException) {
                log.warn("download page {} error, SocketTimeout", request.getUrl());
            } else {
                log.warn("download page {} error", request.getUrl(), var13);
            }
            var9 = page;
        } finally {
            if (httpResponse != null) {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }

            if (this.proxyProvider != null && proxy != null) {
                this.proxyProvider.returnProxy(proxy, page, httpSetting);
            }

        }

        return var9;

    }

    /**
     * 仅仅只把Head部份取回来，用于解析跳转的时候使用，关闭handleResponse即可
     *
     * @param request
     * @param httpSetting
     * @return
     */
    public HttpResult downloadHeader(HttpRequest request, HttpSetting httpSetting) {

        if (httpSetting == null) {
            throw new NullPointerException("task or site can not be null");
        }

        request.setMethod(HttpConstant.Method.HEAD);//只请求头信息

        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = this.getHttpClient(httpSetting);
        ModelProxy proxy = this.proxyProvider != null ? this.proxyProvider.getProxy(httpSetting) : null;
        HttpClientRequestContext requestContext = this.httpUriRequestConverter.convert(request, httpSetting, proxy);
        HttpResult page = new HttpResult();

        HttpResult var9;
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());

            List<URI> locationList = requestContext.getHttpClientContext().getRedirectLocations();

            if (locationList != null && locationList.size() > 0)
                page.setLocationUrl(locationList.get(locationList.size() - 1).toString());

            page.setStatusCode(httpResponse.getStatusLine().getStatusCode());

            // 返回码大于400的，都是错误 ，需要重试请求
            if (httpResponse.getStatusLine().getStatusCode() > 400) {
                log.warn("download page {} error,Status could not be {}", request.getUrl(), httpResponse.getStatusLine().getStatusCode());
                page.setDownloadSuccess(false);
                return page;
            }

            page.setUrl(request.getUrl());

            if (request.isReturnHeader()) {
                page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
            }

            this.log.info("downloading page success {}", request.getUrl());
            HttpResult var8 = page;
            return var8;
        } catch (IOException var13) {

            // 代理里面出现SocketTimeOut太多了，屏蔽掉异常说明
            if (var13 instanceof SocketTimeoutException) {
                log.warn("download page {} error, SocketTimeout", request.getUrl());
            } else {
                log.warn("download page {} error", request.getUrl(), var13);
            }
            var9 = page;
        } finally {
            if (httpResponse != null) {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }

            if (this.proxyProvider != null && proxy != null) {
                this.proxyProvider.returnProxy(proxy, page, httpSetting);
            }

        }

        return var9;

    }


    /**
     * 设置线程数量
     *
     * @param thread
     */
    public void setThread(int thread) {
        this.httpClientGenerator.setPoolSize(thread);
    }

}
