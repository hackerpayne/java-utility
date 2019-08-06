package com.lingdonge.http.webmagic.downloader;

import com.lingdonge.core.http.HttpClientUtils;
import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.http.webmagic.Page;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Site;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.proxy.ProxyProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The http downloader based on HttpClient.
 * 官方原版的基础上增加判断：返回码大于400的直接标记为错误页面，需要重新抓取
 * 这个是不使用连接池的版本，便于进行直接请求，比如豆瓣，如果使用连接池会报异常，这个无连接池的版本会比较占用资源
 */
public class HttpClientDownloaderNoPooling extends AbstractDownloader {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();
    private ProxyProvider proxyProvider;

    public HttpClientDownloaderNoPooling() {
    }

    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }

    public void setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    private CloseableHttpClient getHttpClient(Site site) {
        return HttpClients.createDefault();
    }

    private Set<String> retryKeywords = new HashSet<>();
    private Set<String> mustKeywords = new HashSet<>();

    /**
     * 设置重试关键词，如果内容里面出现任何一个词将会重试
     *
     * @param keyword
     */
    public void addRetryKeywords(String keyword) {
        if (StringUtils.isNotEmpty(keyword)) {
            retryKeywords.add(keyword);
        }
    }

    /**
     * 设置必选关键词，页面必须包含必须关键词，否则会重试请求
     *
     * @param keyword
     */
    public void addMustKeywords(String keyword) {
        // 不为空且没添加过才会添加
        if (StringUtils.isNotEmpty(keyword)) {
            mustKeywords.add(keyword);
        }
    }

    /**
     * 下载页面请求
     *
     * @param request
     * @param task
     * @return
     */
    @Override
    public Page download(Request request, Task task) {

        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }

        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = this.getHttpClient(task.getSite());
        ModelProxy proxy = this.proxyProvider != null ? this.proxyProvider.getProxy(task) : null;
        HttpClientRequestContext requestContext = this.httpUriRequestConverter.convert(request, task.getSite(), proxy);
        Page page = Page.fail();

        Page var9;
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());

            // 返回码大于400的，都是错误 ，需要重试请求
            if (httpResponse.getStatusLine().getStatusCode() > 400) {
                logger.warn("download page {} error,Status could not be {}", request.getUrl(), httpResponse.getStatusLine().getStatusCode());
                page.setDownloadSuccess(false);
                onError(request);
                return page;
            }

            page = this.handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), httpResponse, task);

            List<URI> locationList = requestContext.getHttpClientContext().getRedirectLocations();

            if (locationList != null && locationList.size() > 0) {
                page.setLocationUrl(locationList.get(locationList.size() - 1).toString());
            }

            // 如果页面包含指定关键词，强制进行重试
            boolean retryFlag = false;
            if (retryKeywords.size() > 0) {
                for (String item : retryKeywords) {
                    if (page.getRawText().contains(item)) {
                        retryFlag = true;
                        break;
                    }
                }
            }

            // 如果内容里面不包含必选关键词，强制进行重试
            if (mustKeywords.size() > 0) {
                for (String item : mustKeywords) {
                    if (!page.getRawText().contains(item)) {
                        retryFlag = true;
                        break;
                    }
                }
            }

            if (retryFlag) {
                logger.info("返回内容触发重试关键词，请求将会进行自动重试");
                page.setDownloadSuccess(false);
                onError(request);
                return page;
            }

            this.onSuccess(request);
            this.logger.info("downloading page success {}", request.getUrl());
            Page var8 = page;
            return var8;
        } catch (IOException var13) {

            // 代理里面出现SocketTimeOut太多了，屏蔽掉异常说明
            if (var13 instanceof SocketTimeoutException) {
                this.logger.warn("download page {} error, SocketTimeout", request.getUrl());
            } else {
                this.logger.warn("download page {} error", request.getUrl(), var13);
            }
            this.onError(request);
            var9 = page;
        } finally {
            if (httpResponse != null) {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }

            // 没有使用连接池的话，这个地方是必须要关闭的
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }

            if (this.proxyProvider != null && proxy != null) {
                this.proxyProvider.returnProxy(proxy, page, task);
            }

        }

        return var9;
    }

    /**
     * 只下载Header回来
     *
     * @param request
     * @param task
     * @return
     */
    @Override
    public Page downloadHeader(Request request, Task task) {
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        request.setMethod("HEAD");//只请求头信息

        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = this.getHttpClient(task.getSite());
        ModelProxy proxy = this.proxyProvider != null ? this.proxyProvider.getProxy(task) : null;
        HttpClientRequestContext requestContext = this.httpUriRequestConverter.convert(request, task.getSite(), proxy);
        Page page = Page.fail();

        Page var9;
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());

            // 返回码大于400的，都是错误 ，需要重试请求
            if (httpResponse.getStatusLine().getStatusCode() > 400) {
                logger.warn("download page {} error,Status could not be {}", request.getUrl(), httpResponse.getStatusLine().getStatusCode());
                page.setDownloadSuccess(false);
                onError(request);
                return page;
            }

            List<URI> locationList = requestContext.getHttpClientContext().getRedirectLocations();

            if (locationList != null && locationList.size() > 0)
                page.setLocationUrl(locationList.get(locationList.size() - 1).toString());

            // 如果页面包含指定关键词，强制进行重试
            boolean retryFlag = false;
            if (retryKeywords.size() > 0) {
                for (String item : retryKeywords) {
                    if (page.getRawText().contains(item)) {
                        retryFlag = true;
                        break;
                    }
                }
            }

            // 如果内容里面不包含必选关键词，强制进行重试
            if (mustKeywords.size() > 0) {
                for (String item : mustKeywords) {
                    if (!page.getRawText().contains(item)) {
                        retryFlag = true;
                        break;
                    }
                }
            }

            if (retryFlag) {
                logger.info("返回内容触发重试关键词，请求将会进行自动重试");
                page.setDownloadSuccess(false);
                onError(request);
                return page;
            }

            if (isResponseHeader()) {
                page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
            }

            this.onSuccess(request);
            this.logger.info("downloading page success {}", request.getUrl());
            Page var8 = page;
            return var8;
        } catch (IOException var13) {

            // 代理里面出现SocketTimeOut太多了，屏蔽掉异常说明
            if (var13 instanceof SocketTimeoutException) {
                this.logger.warn("download page {} error, SocketTimeout", request.getUrl());
            } else {
                this.logger.warn("download page {} error", request.getUrl(), var13);
            }
            this.onError(request);
            var9 = page;
        } finally {
            if (httpResponse != null) {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }

            // 没有使用连接池的话，这个地方是必须要关闭的
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }

            if (this.proxyProvider != null && proxy != null) {
                this.proxyProvider.returnProxy(proxy, page, task);
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

    }

}
