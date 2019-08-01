package com.lingdonge.spring.validation.annotation;

import com.lingdonge.core.regex.PatternPool;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

/**
 * 身份证格式校验
 */
@Target(value = {ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdcardValidator.IdCardValidatorInner.class)
@interface IdcardValidator {

    String message() default "不是有效的身份证号码";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 身份证号码校验器
     */
    class IdCardValidatorInner implements ConstraintValidator<IdcardValidator, String> {

        private Pattern moneyPattern = PatternPool.CITIZEN_ID;

        @Override
        public void initialize(IdcardValidator money) {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext arg1) {
            return !StringUtils.isEmpty(value) && moneyPattern.matcher(value).matches();
        }

    }
}