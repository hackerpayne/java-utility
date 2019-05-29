package com.lingdonge.spring.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域过滤器
 */
@Component
@Slf4j
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("CORS filter do filter"));
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 不再对所有请求都添加跨域消息头
        // 在Filter中只对OPTIONS请求进行处理，跨域消息头放在ResponseBodyAdvice中解决
        if (RequestMethod.OPTIONS.toString().equals(req.getMethod())) {
//            String origin = req.getHeader("Origin");
//            String allowMethod = req.getHeader("Access-Control-Request-Method");
//            String allowHeaders = req.getHeader("Access-Control-Request-Headers");

            String origin = "*";
            String allowMethod = "GET, POST, DELETE, PUT";
            String allowHeaders = "Content-Type,X-CAF-Authorization-Token,sessionToken,X-TOKEN";

            resp.setHeader("Access-Control-Allow-Origin", origin);            // 允许指定域访问跨域资源
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setHeader("Access-Control-Max-Age", "86400");            // 浏览器缓存预检请求结果时间,单位:秒
            resp.setHeader("Access-Control-Allow-Methods", allowMethod);  // 允许浏览器在预检请求成功之后发送的实际请求方法名
            resp.setHeader("Access-Control-Allow-Headers", allowHeaders); // 允许浏览器发送的请求消息头

            response.getWriter().println("ok");

            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }
}