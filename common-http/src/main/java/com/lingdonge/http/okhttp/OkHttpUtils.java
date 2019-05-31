package com.lingdonge.http.okhttp;

import com.lingdonge.core.http.UserAgentUtil;
import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.http.HttpMethodConstant;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class OkHttpUtils {

    private static OkHttpClient okHttpClient = null;

    private static Pattern urlPattern = Pattern.compile("(?:url|URL)='(\\S+)'");

    /**
     * @param connectTimeout
     * @param readTimeout
     * @param writeTimeout
     * @param pingInterval
     */
    public OkHttpUtils(Integer connectTimeout, Integer readTimeout, Integer writeTimeout, Integer pingInterval) {
        okHttpClient = getConnect(connectTimeout, readTimeout, writeTimeout, pingInterval);
    }

    /**
     * 返回HTTP连接
     *
     * @param connectTimeout //连接超时 (秒)
     * @param readTimeout    //读超时  (秒)
     * @param writeTimeout   //写超时 (秒)
     * @param pingInterval   //心跳时间 (秒)
     * @return
     */
    public static OkHttpClient getConnect(Integer connectTimeout, Integer readTimeout, Integer writeTimeout, Integer pingInterval) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS);

        if (null != pingInterval && pingInterval > 0) {
            builder.pingInterval(pingInterval, TimeUnit.SECONDS);
        }

        return builder.build();
    }

    /**
     * 返回一个默认的链接对象
     *
     * @return
     */
    public static OkHttpUtils getDefaultConnect() {
        return new OkHttpUtils(10, 30, 10, 30);
    }

    public static void cookieTest() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });
    }

    /**
     * 使用指定的UA请求页面
     *
     * @param url
     * @param ua
     * @param modelProxy 代理配置
     * @return
     */
    public static Document getHtml(String url, String ua, ModelProxy modelProxy) {
        Document doc = null;
        if (StringUtils.isBlank(url)) {
            log.error("getHtmlOk----url:{}---为空-", url);
            return doc;
        }
        OkHttpClient.Builder hcBuilder = OkHttpClientBuilder.getInstance();

        OkHttpClient client = hcBuilder.connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();

        // 设置代理请求
        if (modelProxy != null) {
            hcBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(modelProxy.getHost(), modelProxy.getPort()))).proxyAuthenticator(new Authenticator() {
                @Override
                public okhttp3.Request authenticate(Route arg0, okhttp3.Response response)
                        throws IOException {
                    String credential = Credentials.basic(modelProxy.getUsername(), modelProxy.getPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
            });
        }

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", ua)
                .build();
        Response response;
        try {
            Headers heads = request.headers();
            Set<String> set = heads.names();
            for (String s : set) {
                String value = heads.get(s);
                request.newBuilder().addHeader(s, encodeHeadInfo(value));
            }
            Call call = client.newCall(request);
            response = call.execute();

            doc = Jsoup.parse(response.body().string());
            doc.setBaseUri(response.request().url().toString());

            String urlCnotent = doc.select("meta[http-equiv=refresh]").attr("content");
            Matcher mm = urlPattern.matcher(urlCnotent);
            int refreshCount = 0;
            while (mm.find() && refreshCount < 3) {
                String nextUrl = mm.group(1).replaceAll("'", "");
                doc.setBaseUri(nextUrl);
                doc = getHtml(nextUrl, UserAgentUtil.UAPc, modelProxy);
            }
        } catch (IOException e) {

            log.error("getHtmlOk----url:{}---异常-:{}", url, e.getMessage());
        }
        return doc;
    }

    /**
     * 请求HTML页面内容到String字符串
     *
     * @param url
     * @param ua
     * @param modelProxy
     * @return
     */
    public static String getHtmlString(String url, String ua, ModelProxy modelProxy) {
        String doc = null;
        if (StringUtils.isBlank(url)) {
            log.error("getHtmlOk----url:{}---为空-", url);
            return "";
        }
        OkHttpClient.Builder hcBuilder = OkHttpClientBuilder.getInstance();

        OkHttpClient client = hcBuilder.connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();

        // 设置代理请求
        if (modelProxy != null) {
            hcBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(modelProxy.getHost(), modelProxy.getPort()))).proxyAuthenticator(new Authenticator() {
                @Override
                public okhttp3.Request authenticate(Route arg0, okhttp3.Response response)
                        throws IOException {
                    String credential = Credentials.basic(modelProxy.getUsername(), modelProxy.getPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
            });
        }

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", ua)
                .build();
        Response response;
        try {
            Headers heads = request.headers();
            Set<String> set = heads.names();
            for (String s : set) {
                String value = heads.get(s);
                request.newBuilder().addHeader(s, encodeHeadInfo(value));
            }
            Call call = client.newCall(request);
            response = call.execute();

            doc = response.body().string();
        } catch (IOException e) {

            log.error("getHtmlOk----url:{}---异常-:{}", url, e.getMessage());
        }
        return doc;
    }

    /**
     * 请求HTML页面内容
     *
     * @param url
     * @param ua
     * @param type
     * @param param
     * @param modelProxy
     * @return
     */
    public static Document getHtml(String url, String ua, String type, Map<String, String> param, ModelProxy modelProxy) {
        Document doc = null;
        if (StringUtils.isBlank(url)) {
            log.error("getHtmlOk----url:{}---为空-", url);
            return doc;
        }
        OkHttpClient.Builder hcBuilder = OkHttpClientBuilder.getInstance();

        OkHttpClient client = hcBuilder.build();

        FormBody body = null;
        if (type.toUpperCase().equals("POST")) {
            okhttp3.FormBody.Builder build = new FormBody.Builder();
            for (String key : param.keySet()) {
                build.add(key, param.get(key));
            }
            body = build.build();
        }

        // 设置代理请求
        if (modelProxy != null) {
            hcBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(modelProxy.getHost(), modelProxy.getPort()))).proxyAuthenticator(new Authenticator() {
                @Override
                public okhttp3.Request authenticate(Route arg0, okhttp3.Response response)
                        throws IOException {
                    String credential = Credentials.basic(modelProxy.getUsername(), modelProxy.getPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
            });
        }

        Request request = null;
        if (type.toUpperCase().equals("POST")) {
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("User-Agent", ua)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", ua)
                    .build();
        }

        Response response;
        try {
            Headers heads = request.headers();
            Set<String> set = heads.names();
            for (String s : set) {
                String value = heads.get(s);
                request.newBuilder().addHeader(s, encodeHeadInfo(value));
            }
            response = client.newCall(request).execute();

            doc = Jsoup.parse(response.body().string());
            doc.setBaseUri(response.request().url().toString());

            String urlCnotent = doc.select("meta[http-equiv=refresh]").attr("content");
            Matcher mm = urlPattern.matcher(urlCnotent);
            int refreshCount = 0;
            while (mm.find() && refreshCount < 3) {
                String nextUrl = mm.group(1).replaceAll("'", "");
                doc.setBaseUri(nextUrl);
                doc = getHtml(nextUrl, UserAgentUtil.UAPc, modelProxy);
            }
        } catch (IOException e) {
            log.error("getHtmlOk----url:{}---异常-", url);
//			e.printStackTrace();
        }
        return doc;
    }

    public static String encodeHeadInfo(String headInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0, length = headInfo.length(); i < length; i++) {
            char c = headInfo.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuffer.append(String.format("\\u%04x", (int) c));
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }


    public String postOfSync(String url, String content) {
        Request request = defaultTo(url, content);
        String result = postOfSync(okHttpClient, request);
        return result;
    }

    /**
     * 同步方式提交内容，并返回结果
     *
     * @param url
     * @param content
     * @param keepAlive 自动长连接
     * @param mediaType 内容类型 (application/xml,application/json ...)
     * @param charset   内容编码   (GBK,utf-8 ...)
     * @param cache     缓存 CacheControl
     * @return
     */
    public String postOfSync(String url, String content, boolean keepAlive, String mediaType, String charset, CacheControl cache) {
        Request request = to(url, content, keepAlive, mediaType, charset, cache);
        String result = postOfSync(okHttpClient, request);
        return result;
    }

    /**
     * 把内容发到指定的 url
     *
     * @param url
     * @param formBody  通过键值对的方式传参数  formBody.add(k,v)
     * @param keepAlive 自动长连接
     * @param cache     缓存  CacheControl
     * @return
     */
    public String postOfSync(String url, FormBody.Builder formBody, boolean keepAlive, CacheControl cache) {
        Request request = to(url, formBody, keepAlive, cache);
        String result = postOfSync(okHttpClient, request);
        return result;
    }

    /**
     * 内容发到指定的 url，支持文件及文本框内容同时提交
     *
     * @param url
     * @param multipartBody
     * @param keepAlive     自动长连接
     * @param cache         缓存  CacheControl
     * @return
     */
    public String postOfSync(String url, MultipartBody multipartBody, boolean keepAlive, CacheControl cache) {
        Request request = to(url, multipartBody, keepAlive, cache);
        String result = postOfSync(okHttpClient, request);
        return result;
    }

    /**
     * 同步提交
     *
     * @param okHttpClient
     * @param request
     * @return
     */
    public String postOfSync(OkHttpClient okHttpClient, Request request) {
        String result = "";
        try {
            Response response = okHttpClient.newCall(request).execute(); //同步请求
            result = response.body().string();
            closeTag(okHttpClient, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 把内容发到指定的 url
     *
     * @param url
     * @param formBody 通过键值对的方式传参数  formBody.add(k,v)
     * @param method   提交的方式 POST/GET
     * @param callback 回调func
     * @return
     */
    public Callback ofAsync(String url, FormBody.Builder formBody, HttpMethodConstant method, Callback callback) {
        String reqTag = UUID.randomUUID().toString(); //当前的请求标记ID
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method.name(), formBody.build())
                .tag(reqTag);
        ofAsync(okHttpClient, builder, callback);
        return callback;
    }

    /**
     * 内容发到指定的 url，支持文件及文本框内容同时提交
     *
     * @param url
     * @param multipartBody 文件提交，默认只使用POST
     * @param callback      回调func
     * @return
     */
    public Callback ofAsync(String url, MultipartBody multipartBody, Callback callback) {
        String reqTag = UUID.randomUUID().toString(); //当前的请求标记ID
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(HttpMethodConstant.POST.name(), multipartBody)
                .tag(reqTag);
        ofAsync(okHttpClient, builder, callback);
        return callback;
    }

    /**
     * 异步提交
     *
     * @param okHttpClient
     * @param builder
     * @param callback
     * @return
     */
    public Callback ofAsync(OkHttpClient okHttpClient, Request.Builder builder, Callback callback) {
        okHttpClient.newCall(builder.build()).enqueue(callback);
        //TODO:注意如果你使用了异步提交最好在 callback() 里执行 closeTag()来关闭线程;
        return callback;
    }

    /**
     * 把内容发到指定的 url
     *
     * @param url
     * @param content
     * @param keepAlive 自动长连接
     * @param mediaType 内容类型 (application/xml,application/json ...)
     * @param charset   内容编码   (GBK,utf-8 ...)
     * @param cache     缓存 CacheControl
     * @return
     */
    private Request to(String url, String content, boolean keepAlive, String mediaType, String charset, CacheControl cache) {
        String reqTag = UUID.randomUUID().toString(); //当前的请求标记ID
        RequestBody body = RequestBody.create(MediaType.parse(mediaType + "; charset=" + charset), content); //内容

        Request request = to(url, body, keepAlive, cache);
        return request;
    }

    /**
     * 把内容发到指定的 url
     *
     * @param url
     * @param formBody  通过键值对的方式传参数  formBody.add(k,v)
     * @param keepAlive
     * @param cache
     * @return
     */
    private Request to(String url, FormBody.Builder formBody, boolean keepAlive, CacheControl cache) {
        String reqTag = UUID.randomUUID().toString(); //当前的请求标记ID
        Request request = to(url, formBody.build(), keepAlive, cache);
        return request;
    }

    /**
     * @param url
     * @param multipartBody 可以传参数，同时也可以上传文件
     * @param keepAlive
     * @param cache
     * @return
     */
    private Request to(String url, MultipartBody multipartBody, boolean keepAlive, CacheControl cache) {
        String reqTag = UUID.randomUUID().toString(); //当前的请求标记ID
        Request request = to(url, (RequestBody) multipartBody, keepAlive, cache);
        return request;
    }


    private Request to(String url, RequestBody body, boolean keepAlive, CacheControl cache) {
        String reqTag = UUID.randomUUID().toString(); //当前的请求标记ID
        Request.Builder builder = new Request.Builder();
        if (keepAlive) {
            builder.addHeader("connection", "Keep-Alive");
        }
        builder.url(url)
                .post(body)
                .addHeader("accept", "*/*")
                .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                .tag(reqTag)//当前的请求标记ID
                .cacheControl(cache);

        Request request = builder.build();
        return request;
    }

    private Request defaultTo(String url, String content) {
        String reqTag = UUID.randomUUID().toString(); //当前的请求标记ID
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content); //内容
        Request.Builder builder = new Request.Builder();
        Request request = to(url, body, false, CacheControl.FORCE_NETWORK); //不缓存
        return request;
    }

    /**
     * 关闭当前请求
     *
     * @param okHttpClient
     * @param request
     */
    public void closeTag(OkHttpClient okHttpClient, Request request) {
        if (null == request || null == request.tag() || null == okHttpClient) {
            return;
        }
        Object tag = request.tag();
        synchronized (okHttpClient.dispatcher().getClass()) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }

}
