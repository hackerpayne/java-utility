package com.lingdonge.push.configuration.properties;

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

    private Ali ali;

    private JiGuang jiGuang;

    @Getter
    @Setter
    public static class Ali {
        /**
         *
         */
        private String accessKeyId;

        /**
         *
         */
        private String accessKeySecret;

        /**
         *
         */
        private String androidAppKey;

        /**
         *
         */
        private String iosAppKey;

        /**
         *
         */
        private String regionId;

        private boolean iosApnsProduction;
    }

    @Getter
    @Setter
    public static class JiGuang {

        private String iosAppKey;

        private String iosMasterSecret;

        /**
         * IOS才设置，上线为true，没上线为false
         */
        private boolean iosApnsProduction;

        private String androidAppKey;

        private String androidMasterSecret;

        private Integer liveTime;
    }

}

