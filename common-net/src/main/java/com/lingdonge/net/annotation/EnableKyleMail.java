package com.lingdonge.net.annotation;

import com.lingdonge.net.configuration.MailAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 CORSConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MailAutoConfiguration.class)
@Documented
public @interface EnableKyleMail {
}