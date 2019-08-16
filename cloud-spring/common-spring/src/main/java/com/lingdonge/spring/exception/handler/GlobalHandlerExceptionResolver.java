package com.lingdonge.spring.exception.handler;

import com.google.common.base.Joiner;
import com.lingdonge.spring.enums.RespStatusEnum;
import com.lingdonge.spring.exception.BizException;
import com.lingdonge.spring.bean.response.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
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
     * 处理自定义的业务异常
     *
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    public Resp bizExceptionHandler(HttpServletRequest req, BizException ex) {
        log.error(ex.getMessage(), ex);
        return Resp.fail(ex.getCode(), ex.getMessage());
    }

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
        log.error("MethodArgumentNotValidException参数校验异常", ex);
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
    public Resp<String> handleConstraintViolationException(ValidationException ex) {
        List<String> errorMsg = new ArrayList<>();
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) ex;

            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                errorMsg.add(item.getMessage());
            }
        }
        log.error("ConstraintViolationException参数校验异常", ex);
        return Resp.fail(RespStatusEnum.PARAMATER_ILLEGAL.getCode(), Joiner.on(",").join(errorMsg));
    }

    /**
     * 路径不存在异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Resp<String> handlerNoFoundException(Exception ex) {
        log.error("NoHandlerFoundException路径不存在异常", ex);
        return Resp.fail(RespStatusEnum.PAGE_NOT_FOUND);
    }

    /**
     * 空指针异常
     *
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    public Resp<String> exceptionHandler(HttpServletRequest req, NullPointerException ex) {
        log.error("NullPointerException空指针异常", ex);
        return Resp.fail(RespStatusEnum.NULL);
    }

    /**
     * 处理未捕获的 RuntimeException 异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    public Resp handleRuntimeException(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return Resp.fail(RespStatusEnum.FAIL);
    }

    /**
     * 默认全局异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Resp resolveException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return Resp.fail(RespStatusEnum.FAIL);
    }
//    /**
//     * 默认全局异常处理
//     *
//     * @param request
//     * @param response
//     * @param handler
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler(Exception.class)
//    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        String uri = request.getRequestURI();
//        try {
//            log.error("未知异常", ex);
//            SpringRequestUtil.writeJson(response, Resp.fail(ex.getMessage()));
//        } catch (Exception e) {
//            log.error(StrUtil.format("未知异常,URI = {}", uri), ex);
//            return null;
//        }
//        return null;
//    }

}