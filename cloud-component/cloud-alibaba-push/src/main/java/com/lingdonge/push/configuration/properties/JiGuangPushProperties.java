package com.lingdonge.push.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jpush")
@Data
public class JiGuangPushProperties {

    private String iosAppkey;

    private String iosMasterSecret;

    private String androiAppkey;

    private String androidMasterSecret;

    private Integer liveTime;

}

