package com.lingdonge.db.annotation;

import com.lingdonge.db.configuration.DynamicDataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DynamicDataSourceAutoConfiguration.class})
@Documented
public @interface EnableDynamicDataSource {
}