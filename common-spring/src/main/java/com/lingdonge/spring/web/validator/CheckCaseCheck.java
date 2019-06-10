package com.lingdonge.spring.web.validator;

import com.lingdonge.spring.enums.CaseModeEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Hibernate Validator 大小写校验工具
 * 使用时：@CheckCaseCheck(value = CaseModeEnum.LOWER,message = "userName必须是小写")
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckCaseValidator.class)
@Documented
public @interface CheckCaseCheck {

    /**
     * 错误提示消息
     *
     * @return
     */
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 获取验校值
     *
     * @return
     */
    CaseModeEnum value();
}