package com.lingdonge.spring.validation.annotation;

import com.lingdonge.spring.enums.CaseModeEnum;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Hibernate Validator 大小写校验工具
 * 使用时：@CaseCheckValidator(value = CaseModeEnum.LOWER,message = "userName必须是小写")
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CaseCheckValidator.CheckCaseValidatorInner.class)
@Documented
public @interface CaseCheckValidator {

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


    /**
     * Hibernate大小写校验器
     */
    class CheckCaseValidatorInner implements ConstraintValidator<CaseCheckValidator, String> {

        private CaseModeEnum caseMode;

        @Override
        public void initialize(CaseCheckValidator caseCheckValidator) {
            this.caseMode = caseCheckValidator.value();
        }

        @Override
        public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
            if (s == null) {
                return true;
            }

            if (caseMode == CaseModeEnum.UPPER) {
                return s.equals(s.toUpperCase());
            } else {
                return s.equals(s.toLowerCase());
            }
        }
    }

}