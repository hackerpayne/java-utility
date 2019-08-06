package com.lingdonge.spring.log;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Log4J过滤器
 * 微服务日志组件过滤
 * MDC使用Map机制来存储信息，信息以key/value对的形式存储在Map中。
 * 使用Servlet Filter或者AOP在服务接收到请求时，获取需要的值填充MDC。在log4j2的配置中就可以使用%X{key}打印MDC中的内容，从而识别出同一次请求中的log。
 * log4j 1.x中使用 MDC.clear();
 * log4j 2.x中使用 ThreadContext.remove(Contents.REQUEST_ID);
 * https://blog.csdn.net/oMaverick1/article/details/80360171
 * 使用日志格式：<PatternLayout pattern="|%d{yyyy-MM-dd HH:mm:ss.SSS}|%5p|%5t|%4c:%L|%X{traceid}|%X{clientaddr}|%X{serveraddr}|%X{cientsysname}|%X{serversysname}|%X{transid}|%X{logType}|%m|%n" />
 */
public class MdcLogfilter implements Filter {

    private String systemName;

    public MdcLogfilter(String systemName) {
        this.systemName = systemName;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            insertIntoMDC(request);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
    }

    protected void insertIntoMDC(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String traceId = httpRequest.getHeader(TRACE_ID);
//        String clientAddr = httpRequest.getRemoteAddr();
//        String serverAddr = getServerAddr();
//        String clientSysName = httpRequest.getHeader(CLIENT_SYS_NAME);
//        String serverSysName = systemName;
//        String transId = (String) httpRequest.getAttribute(TRANS_ID);
//
//        MDC.put(TRACE_ID, traceId);
//        MDC.put(CLIENT_ADDR, clientAddr);
//        MDC.put(SERVER_ADDR, serverAddr);
//        MDC.put(CLIENT_SYS_NAME, clientSysName);
//        MDC.put(SERVER_SYS_NAME, serverSysName);
//        MDC.put(TRANS_ID, transId);
//        MDC.put(LOG_TYPE, LogTypeEnum.REQ.getKey());
    }


}