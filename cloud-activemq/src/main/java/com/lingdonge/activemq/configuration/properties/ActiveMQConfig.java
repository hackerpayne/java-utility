package com.lingdonge.activemq.configuration.properties;

import com.lindonge.core.bean.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 使用单独的配置去集成ActiveMQ的外围服务
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveMQConfig extends BaseEntity {

    private String brokerUrl;

    private String user;

    private String password;

}
