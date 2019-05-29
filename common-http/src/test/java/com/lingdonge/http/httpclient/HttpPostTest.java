package com.lingdonge.http.httpclient;

import com.alibaba.fastjson.JSON;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HttpPostTest {

    /**
     * 表单提交
     */
    public static void postForm() throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://api.example.com/");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "Adam_DENG"));
        params.add(new BasicNameValuePair("password", "password"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode());
        client.close();

    }

    /**
     * Json上传
     *
     * @throws IOException
     */
    public static void postJson() throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("");

        // 这里使用了对象转为json string
        String json = JSON.toJSONString("{}");// 对象转Json
        StringEntity entity = new StringEntity(json, "UTF-8");
        // NOTE：防止中文乱码
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json; charset=UTF-8");
        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode());
        client.close();

    }

    /**
     * 文件上传
     *
     * @throws IOException
     */
    public static void postFile() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 这里支持 `File`, 和 `InputStream` 格式, 对你来说哪个方便， 使用哪个。
        // 因为我的文件是从 `URL` 拿的，所以代码如下， 其他形式类似
        InputStream is = new URL("http://api.demo.com/file/test.txt").openStream();
        builder.addBinaryBody("file", is, ContentType.APPLICATION_OCTET_STREAM, "test.txt");

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = client.execute(httpPost);
    }

    /**
     * 使用FileEntity来上传文件
     */
    public static void testPostFileWith() {
        File file = new File("somefile.txt");
        FileEntity entity = new FileEntity(file, ContentType.create("text/plain", "UTF-8"));
        HttpPost httppost = new HttpPost("http://localhost/action.do");
        httppost.setEntity(entity);
    }

    /**
     * 上传附件的同时传递 Url 参数
     */
    public static void postFileAndPara() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        InputStream is = new URL("http://api.demo.com/file/test.txt").openStream();
        builder.addBinaryBody("file", is, ContentType.APPLICATION_OCTET_STREAM, "test.txt");

        StringBody realnameBody = new StringBody("Adam_DENG", ContentType.create("text/plain", Charset.forName("UTF-8")));
        builder.addPart("realname", realnameBody);

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        CloseableHttpResponse response = client.execute(httpPost);
    }

    /**
     * 通常，我们推荐让HttpClient选择基于被传递的HTTP报文属相最合适的传输编码方式。可能地，可以通过设置HttpEntity#setChunked()为true来通知HttpClient你要进行分块编码。注意HttpClient将会使用这个标志作为提示。当使用一些不支持分块编码的HTTP版本（比如HTTP/1.0.）时，这个值将会忽略。
     */
    public static void testPostWithChunk() {
        StringEntity entity = new StringEntity("important message", ContentType.create("plain/text", Consts.UTF_8));
        entity.setChunked(true);
        HttpPost httppost = new HttpPost("http://localhost/acrtion.do");
        httppost.setEntity(entity);
    }

}
