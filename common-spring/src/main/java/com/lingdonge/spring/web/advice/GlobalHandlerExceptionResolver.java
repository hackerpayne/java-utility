package com.lingdonge.spring.web.advice;

import com.lingdonge.spring.restful.Resp;
import com.lingdonge.spring.web.SpringRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常统一处理。使用Resp做为统一返回值
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalHandlerExceptionResolver {

    @ExceptionHandler(Exception.class)
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String uri = request.getRequestURI();
        try {
            log.error("GlobalHandlerExceptionResolver", ex.getMessage(), ex);
            SpringRequestUtil.writeJson(response, Resp.fail(ex.getMessage()));
        } catch (Exception e) {
            log.error("GlobalHandlerExceptionResolver fail to response,URI = {}", uri, e, ex);
            return null;
        }
        return null;
    }
}