package com.lingdonge.push.configuration.properties;

import com.lingdonge.push.bean.PushSenderAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ConfigurationProperties(prefix = "push")
@Getter
@Setter
public class PushProperties implements Serializable {

    /**
     * 默认发送类
     */
    private String defaultSender;

    /**
     * 创蓝短信配置
     */
    private PushSenderAccount pushSenderAccount;

}

