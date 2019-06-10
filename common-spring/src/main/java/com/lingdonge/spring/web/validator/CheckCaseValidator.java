package com.lingdonge.spring.web.validator;

import com.lingdonge.spring.enums.CaseModeEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Hibernate大小写校验器
 * 使用时：@CheckCaseCheck(value = CaseModeEnum.LOWER,message = "userName必须是小写")
 */
public class CheckCaseValidator implements ConstraintValidator<CheckCaseCheck, String> {

    private CaseModeEnum caseMode;

    @Override
    public void initialize(CheckCaseCheck checkCaseCheck) {
        this.caseMode = checkCaseCheck.value();
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
