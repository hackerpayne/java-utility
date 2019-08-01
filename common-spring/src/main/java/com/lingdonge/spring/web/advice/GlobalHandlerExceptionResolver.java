package com.lingdonge.spring.web.advice;

import com.google.common.base.Joiner;
import com.lingdonge.spring.restful.Resp;
import com.lingdonge.spring.web.SpringRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 全局异常统一处理。使用Resp做为统一返回值
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalHandlerExceptionResolver {

    /**
     * 方法和参数校验异常
     * 解决 validator 的异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Resp<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        List<String> errorMsg = new ArrayList<>();

        for (FieldError error : fieldErrors) {
            errorMsg.add(error.getDefaultMessage());
        }
        log.error(ex.getMessage(), ex);
        return Resp.fail(Joiner.on(",").join(errorMsg));
    }

    /**
     * ValidationException异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Resp<String> handleValidationException(ValidationException ex) {
        List<String> errorMsg = new ArrayList<>();
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) ex;

            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                errorMsg.add(item.getMessage());
            }
        }
        log.error(ex.getMessage(), ex);
        return Resp.fail(Joiner.on(",").join(errorMsg));
    }

    /**
     * ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Resp<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        return Resp.fail(ex.getMessage());
    }

    /**
     * 路径不存在异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Resp<String> handlerNoFoundException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return Resp.fail(404, "路径不存在，请检查路径是否正确");
    }

    /**
     * 默认全局异常处理
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
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