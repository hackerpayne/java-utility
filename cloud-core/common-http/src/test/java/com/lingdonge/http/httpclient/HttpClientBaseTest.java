package com.lingdonge.http.httpclient;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpClientBaseTest {

    /**
     * 使用UrlBuilder构建URL
     *
     * @throws URISyntaxException
     */
    public void buildUrl() throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.google.com")
                .setPath("/search")
                .setParameter("q", "httpclient")
                .setParameter("btnG", "Google Search")
                .setParameter("aq", "f")
                .setParameter("oq", "")
                .build();

        HttpGet httpget = new HttpGet(uri);

    }

    /**
     * 处理Response响应
     */
    public void testResponse() {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());

        // 添加Header头部信息
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("Set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");
        Header h1 = response.getFirstHeader("Set-Cookie");
        System.out.println(h1);

        Header h2 = response.getLastHeader("Set-Cookie");
        System.out.println(h2);

        Header[] hs = response.getHeaders("Set-Cookie");
        System.out.println(hs.length);

        // 遍历头部元素
        HeaderIterator it = response.headerIterator("Set-Cookie");
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        // 转换为单个元素
        HeaderElementIterator iter = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
        while (iter.hasNext()) {
            HeaderElement elem = iter.nextElement();
            System.out.println(elem.getName() + " = " + elem.getValue());
            NameValuePair[] params = elem.getParameters();
            for (int i = 0; i < params.length; i++) {
                System.out.println(" " + params[i]);
            }
        }
    }

    /**
     * Entity实体的处理
     *
     * @throws IOException
     */
    public void testEntity() throws IOException {
        StringEntity myEntity = new StringEntity("important message", ContentType.create("text/plain", "UTF-8"));
        System.out.println(myEntity.getContentType());
        System.out.println(myEntity.getContentLength());

        System.out.println(EntityUtils.toString(myEntity));

        System.out.println(EntityUtils.toByteArray(myEntity).length);

    }
}
