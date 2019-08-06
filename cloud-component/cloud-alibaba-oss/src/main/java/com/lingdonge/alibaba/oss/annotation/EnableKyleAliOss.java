package com.lingdonge.alibaba.oss.annotation;


import com.lingdonge.alibaba.oss.configuration.AlibabaOssAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 阿里巴巴 OSS 服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({AlibabaOssAutoConfiguration.class})
@Documented
public @interface EnableKyleAliOss {
}