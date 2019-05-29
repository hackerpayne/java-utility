package com.lingdonge.http.httpclient;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

/**
 * 上下文管理
 */
@Getter
@Setter
public class HttpClientRequestContext {

    private HttpUriRequest httpUriRequest;

    private HttpClientContext httpClientContext;

}
