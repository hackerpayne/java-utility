package com.lingdonge.push.annotation;


import com.lingdonge.push.configuration.JiGuangPushAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 push 发送配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({JiGuangPushAutoConfiguration.class})
@Documented
public @interface EnableKylePushJiGuang {
}

