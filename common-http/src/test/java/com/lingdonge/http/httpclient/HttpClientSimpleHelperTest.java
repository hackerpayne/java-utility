package com.lingdonge.http.httpclient;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpClientSimpleHelperTest {

    @Test
    public void getHtml() throws IOException {

        String html = HttpClientSimpleHelper.getHtml("http://www.baidu.com");
        System.out.println(html);
    }

    public class MyJsonObject {
        private String name;
        private String value;
    }

    public void testGetHtmlWithHandler() throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://localhost/json");

        ResponseHandler<MyJsonObject> rh = new ResponseHandler<MyJsonObject>() {

            @Override
            public MyJsonObject handleResponse(final HttpResponse response) throws IOException {
                StatusLine statusLine = response.getStatusLine();
                HttpEntity entity = response.getEntity();

                if (statusLine.getStatusCode() >= 300) {
                    throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                }

                if (entity == null) {
                    throw new ClientProtocolException("Response contains no content");
                }

                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                return JSON.parseObject(HttpClientUtil.handleResponseToStr(response), MyJsonObject.class);
            }
        };
        MyJsonObject myjson = httpclient.execute(httpget, rh);
    }
}
