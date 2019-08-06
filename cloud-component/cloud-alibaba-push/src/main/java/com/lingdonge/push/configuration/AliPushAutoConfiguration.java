package com.lingdonge.push.configuration;

import com.lingdonge.push.configuration.properties.AliPushProperties;
import com.lingdonge.push.service.AliMessagePushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

@EnableConfigurationProperties(AliPushProperties.class)
@Slf4j
public class AliPushAutoConfiguration {

    @Resource
    private AliPushProperties aliPushProperties;

    @Bean
    public AliMessagePushService aliMessagePushUtil() {
        log.info("<<<<<<<<<<<<<<< 加载 Alibaba AliMessagePushUtil 服务 >>>>>>>>>>>>>>>>>>");
        return new AliMessagePushService(aliPushProperties);
    }

}
