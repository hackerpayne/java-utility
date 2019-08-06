package com.lingdonge.spring.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.StreamUtils;

/**
 * 从请求体中获取参数请求包装类
 * 解决拦截器之后，流无法被重复读取，造成RequestBody抛异常：Required request body is missing
 * 逻辑说明：继承HttpServletRequestWrapper类，将请求体中的流copy一份出来，覆写getInputStream()和getReader()方法供外部使用。
 * 如下，每次调用覆写后的getInputStream()方法都是从复制出来的二进制数组中进行获取，这个二进制数组在对象存在期间一直存在，这样就实现了流的重复读取。
 * <p>
 * 使用：需要配合BodyReaderFilter ，先添加Filter替换掉默认的Request对象
 * 参考：https://blog.csdn.net/qq_33517683/article/details/78593487
 * https://www.njlife.top/2018/09/05/springboot%E8%AF%B7%E6%B1%82%E4%BD%93%E4%B8%AD%E7%9A%84%E6%B5%81%E5%8F%AA%E8%83%BD%E8%AF%BB%E5%8F%96%E4%B8%80%E6%AC%A1%E7%9A%84%E9%97%AE%E9%A2%98/
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody = null;//用于将流保存下来

    /**
     * HttpServletRequest 复制出来，以便重用
     * @param request
     * @throws IOException
     */
    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        requestBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}