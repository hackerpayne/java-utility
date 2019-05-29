package com.lingdonge.spring.validation.annotation;

import org.apache.commons.lang3.time.DateUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.util.Date;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义注解实现校验工具示例，
 * 使用时：
 * \@DateValidator(dateFormat = "yyyy-MM-dd") 添加到Field字段上实现
 * <p>
 * 注意点：
 * 1、自定义注解 必须包含 message ,groups,payload 属性。
 * 2、注解上必须有 @Constraint(validatedBy = {******.class}) 注解标注，validateBy 的值就是校验逻辑的实现类，实现类必须实现接口ConstraintValidator
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {DateValidator.DateValidatorInner.class})
public @interface DateValidator {

    /**
     * 必须的属性
     * 显示 校验信息
     * 利用 {} 获取 属性值，参考了官方的message编写方式
     *
     * @see org.hibernate.validator 静态资源包里面 message 编写方式
     */
    String message() default "日期格式不匹配{dateFormat}";

    /**
     * 必须的属性
     * 用于分组校验
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 非必须
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 必须实现 ConstraintValidator接口
     */
    class DateValidatorInner implements ConstraintValidator<DateValidator, String> {
        private String dateFormat;

        @Override
        public void initialize(DateValidator constraintAnnotation) {
            this.dateFormat = constraintAnnotation.dateFormat();
        }

        /**
         * 校验逻辑的实现
         *
         * @param value 需要校验的 值
         * @return 布尔值结果
         */
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            if ("".equals(value)) {
                return true;
            }
            try {
                Date date = DateUtils.parseDate(value, dateFormat);
                return date != null;
            } catch (ParseException e) {
                return false;
            }
        }
    }
}
