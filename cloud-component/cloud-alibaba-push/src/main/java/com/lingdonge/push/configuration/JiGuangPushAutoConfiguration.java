package com.lingdonge.push.configuration;

import com.lingdonge.push.configuration.properties.JiGuangPushProperties;
import com.lingdonge.push.service.JiGuangPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

@EnableConfigurationProperties(JiGuangPushProperties.class)
@Slf4j
public class JiGuangPushAutoConfiguration {

    @Resource
    private JiGuangPushProperties jiGuangPushProperties;

    @Bean
    public JiGuangPushService jiGuangPushService() {
        log.info("<<<<<<<<<<<<<<< 加载 Alibaba JiGuangPushService 服务 >>>>>>>>>>>>>>>>>>");

        boolean devEnviroment = false;
        return new JiGuangPushService(jiGuangPushProperties, devEnviroment);
    }

}
