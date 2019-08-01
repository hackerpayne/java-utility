package com.lingdonge.spring.validation.annotation;

import com.lingdonge.core.regex.PatternPool;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 身份证格式校验
 */
@Target(value = {FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UserNameValidator.UserNameValidatorInner.class)
@interface UserNameValidator {

    String message() default "用户名格式无效";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 身份证号码校验器
     */
    class UserNameValidatorInner implements ConstraintValidator<UserNameValidator, String> {

        @Override
        public void initialize(UserNameValidator validator) {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext arg1) {
            return !StringUtils.isEmpty(value) && PatternPool.USER_NAME.matcher(value).matches();
        }

    }
}