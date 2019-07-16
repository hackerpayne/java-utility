package com.lingdonge.spring.configuration;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 让Hibernate支持GET里面校验
 * 使用时：
 * 1、Controller上加注解@Validated
 * 2、然后就可以使用了
 */
@Configuration
@Slf4j
public class HibernateValidatorAutoConfiguration {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        // 设置validator模式为快速失败返回,默认是普通模式，会返回所有的验证不通过信息集合
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @Bean
    public Validator validator() {

        log.info("<<<<<<<<<<<<<<< 启用 Hibernate FastFail 快速失败模式 >>>>>>>>>>>>>>>>>>");

        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        return validator;
    }


}
