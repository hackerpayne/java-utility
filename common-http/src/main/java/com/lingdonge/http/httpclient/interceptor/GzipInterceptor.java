package com.lingdonge.http.httpclient.interceptor;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * 拦截器，允许Gzip压缩
 */
public class GzipInterceptor implements HttpRequestInterceptor {
    @Override
    public void process(HttpRequest request, HttpContext httpContext) throws HttpException, IOException {
        if (!request.containsHeader("Accept-Encoding")) {
            request.addHeader("Accept-Encoding", "gzip");
        }
    }
}
