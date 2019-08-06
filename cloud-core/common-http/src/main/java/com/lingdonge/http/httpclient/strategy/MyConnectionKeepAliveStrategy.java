package com.lingdonge.http.httpclient.strategy;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * 解决豆瓣http采集时的连接池使用问题，参考：https://www.cnblogs.com/aisam/p/7652680.html
 */
public class MyConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {


    private Integer timeOut = 20;

    public MyConnectionKeepAliveStrategy() {

    }

    public MyConnectionKeepAliveStrategy(Integer timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public long getKeepAliveDuration(HttpResponse httpResponse,
                                     HttpContext httpContext) {
        return this.timeOut; // 20 seconds,because tomcat default keep-alive timeout is 20s
    }

}
