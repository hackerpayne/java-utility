package com.lingdonge.http.httpclient;

import com.lindonge.core.http.HttpClientUtils;
import com.lingdonge.http.HttpRequest;
import com.lingdonge.http.HttpResult;
import com.lingdonge.http.util.CharsetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Util工具类
 */
@Slf4j
public class HttpClientUtil {


    /**
     * @param postData
     * @param useJson
     * @return
     */
    public static HttpEntity buildStringEntity(String postData, Boolean useJson) {
        // StringEntity处理任意格式字符串请求参数
        StringEntity stringEntity = new StringEntity(postData, "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        if (useJson) {
            stringEntity.setContentType("application/json");
        } else {
//            stringEntity.setContentType(contentType);
        }

        return stringEntity;
    }

    /**
     * Map 转 UrlEncodedFormEntity
     *
     * @param postData
     * @return
     */
    public static HttpEntity buildUrlEncodeEntity(Map postData) {
        //设置参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Iterator iter = postData.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String value = String.valueOf(postData.get(name));
            nvps.add(new BasicNameValuePair(name, value));
            //System.out.println(name +"-"+value);
        }
        return new UrlEncodedFormEntity(nvps, Consts.UTF_8);
    }

    /**
     * 直接转成字符串
     *
     * @param response
     * @return
     */
    public static String handleResponseToStr(HttpResponse response) throws IOException {
        return handleResponseToStr(response, Consts.UTF_8);
    }

    /**
     * Response读取到String里面，不推荐，这样会复制出来处理，推荐使用Handler解决
     * res = EntityUtils.toString(response.getEntity(),"UTF-8");
     * EntityUtils.consume(response1.getEntity());
     *
     * @param response
     * @return
     * @throws IOException
     */
    public static String handleResponseToStr(HttpResponse response, Charset charset) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity, charset == null ? Consts.UTF_8 : charset);
        }
        return null;
    }


    /**
     * @param request
     * @param charset
     * @param httpResponse
     * @return
     * @throws IOException
     */
    public static HttpResult handleResponse(HttpRequest request, String charset, HttpResponse httpResponse) throws IOException {
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        HttpResult page = new HttpResult();
        page.setBytes(bytes);
        if (!request.isBinaryContent()) {
            if (null == charset) {
                charset = getHtmlCharset(contentType, bytes);
            }

            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }

        page.setUrl(request.getUrl());
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);

        if (request.isReturnHeader()) {
            page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }

        return page;
    }

    /**
     * 直接处理返回的信息到HttpResult里面
     *
     * @param httpResponse
     * @return
     * @throws IOException
     */
    public static HttpResult handleResponse(HttpResponse httpResponse) throws IOException {

        HttpResult httpResult = new HttpResult();

        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();

        httpResult.setBytes(bytes);

        String charset = getHtmlCharset(contentType, bytes);
        httpResult.setCharset(charset);

        httpResult.setRawText(new String(bytes, charset));
        httpResult.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        httpResult.setDownloadSuccess(true);
        httpResult.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));

        return httpResult;
    }


    /**
     * 结果保存至文件内
     *
     * @param httpResponse
     * @param saveFile
     * @throws IOException
     */
    public static void handleResponseToFile(HttpResponse httpResponse, String saveFile) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();

            File file = new File(saveFile);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
        }

    }

    /**
     * 检测页面编码
     *
     * @param contentType
     * @param contentBytes
     * @return
     * @throws IOException
     */
    private static String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Charset.defaultCharset().name();
            log.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }

        return charset;
    }

    /**
     * 解压
     *
     * @param entity
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String unGZipContent(HttpEntity entity, String encoding) throws IOException {
        String responseContent = "";
        GZIPInputStream gis = new GZIPInputStream(entity.getContent());
        int count = 0;
        byte data[] = new byte[1024];
        while ((count = gis.read(data, 0, 1024)) != -1) {
            String str = new String(data, 0, count, encoding);
            responseContent += str;
        }
        return responseContent;
    }

    /**
     * 压缩
     *
     * @param sendData
     * @return
     * @throws IOException
     */
    public static ByteArrayOutputStream gZipContent(String sendData) throws IOException {
        if (StringUtils.isBlank(sendData)) {
            return null;
        }

        ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
        originalContent.write(sendData.getBytes("UTF-8"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        originalContent.writeTo(gzipOut);
        gzipOut.close();
        return baos;
    }

    /**
     * 关闭Entity实体
     *
     * @param entity
     * @throws IOException
     */
    public static void close(HttpEntity entity) throws IOException {
        if (entity == null) {
            return;
        }
        if (entity.isStreaming()) {
            final InputStream instream = entity.getContent();
            if (instream != null) {
                instream.close();
            }
        }
    }

    /**
     * 创建一个二进制的Response读取器
     *
     * @return
     */
    public static ResponseHandler<byte[]> buildBytesHandler() {
        ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
            @Override
            public byte[] handleResponse(HttpResponse response) throws IOException {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toByteArray(entity);
                } else {
                    return null;
                }
            }
        };
        return handler;
    }


}
