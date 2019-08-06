package com.lingdonge.alibaba.oss.configuration;

import com.lingdonge.alibaba.oss.service.OssClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(OssProperties.class) // 使用OSS配置文件读取
@Slf4j
public class AlibabaOssAutoConfiguration {

    @Resource
    private OssProperties ossProperties;

    /**
     * 创建Bean实例
     *
     * @return
     */
    @Bean
    public OssClientService ossClientService() {
        log.info("<<<<<<<<<<<<<<< 加载 Alibaba OssClientService 服务 >>>>>>>>>>>>>>>>>>");
        return new OssClientService(ossProperties);
    }
}
