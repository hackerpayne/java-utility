package com.lingdonge.http.okhttp;

import com.lingdonge.http.httpclient.utils.HttpClientSslUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
public class OkHttpClientBuilderBox {

    public OkHttpClient.Builder hcBuilder = null;

    /**
     * 构造函数，信任SSL证书
     */
    public OkHttpClientBuilderBox() {
        hcBuilder = new OkHttpClient.Builder();
        try {
            hcBuilder.sslSocketFactory(HttpClientSslUtil.buildSslSocketFactory(), HttpClientSslUtil.createX509TrustManager())
                    .hostnameVerifier(HttpClientSslUtil.createHostNameVerifier());
        } catch (Exception e) {
            log.error("ssl init fail.err={}", e.getMessage(), e);
        }
    }

    public OkHttpClient.Builder instance() {
        return this.hcBuilder;
    }

}
