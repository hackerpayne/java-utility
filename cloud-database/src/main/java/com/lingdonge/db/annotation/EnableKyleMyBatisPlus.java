package com.lingdonge.db.annotation;

import com.lingdonge.db.configuration.MyBatisPlusAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 MybatisPlus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MyBatisPlusAutoConfiguration.class})
@Documented
public @interface EnableKyleMyBatisPlus {
}