package com.lingdonge.spring.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

/**
 * 文件上传配置
 * 其实没有必要，可以直接通过配置文件设置
 */
//@Configuration
@Slf4j
public class MultipartConfiguration {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        log.info("<<<<<<<<<<<<<<< 文件上传配置初始化加载 >>>>>>>>>>>>>>>>>>");

        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize(DataSize.ofMegabytes(500)); // 500MB
//        factory.setMaxRequestSize(DataSize.ofMegabytes(500)); // 500MB
        return factory.createMultipartConfig();
    }
}
