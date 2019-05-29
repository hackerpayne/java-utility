package com.lingdonge.spring.configuration;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/**
 * 配置，加载EventBus事件总线
 */
//@Configuration
@Slf4j
public class EventBusConfiguration {

    /**
     * 定义事件总线bean
     *
     * @return
     */
    @Bean
    public EventBus eventBus() {
        log.info("<<<<<<<<<<<<<<< 加载 Google EventBus消息总线 服务 >>>>>>>>>>>>>>>>>>");
        return new EventBus();
    }

}
