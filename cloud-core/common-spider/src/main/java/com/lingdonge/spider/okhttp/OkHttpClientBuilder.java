package com.lingdonge.spider.okhttp;

import okhttp3.OkHttpClient;

public class OkHttpClientBuilder {

    private static class OkHttpclientBuilderProviderHolder {
        public static OkHttpClientBuilderBox okHttpClientBuilderBox = new OkHttpClientBuilderBox();
    }

    public static OkHttpClient.Builder getInstance(){
        return OkHttpclientBuilderProviderHolder.okHttpClientBuilderBox.instance();
    }
}
