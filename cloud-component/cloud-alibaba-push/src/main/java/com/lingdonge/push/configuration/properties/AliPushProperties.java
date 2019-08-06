package com.lingdonge.push.configuration.properties;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alibaba.push")
@Getter
@Setter
public class AliPushProperties extends BaseEntity {

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
}
