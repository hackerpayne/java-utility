package com.lingdonge.push.bean;

import com.lingdonge.core.bean.base.BaseEntity;

import lombok.Data;

@Data
public class PushSenderAccount extends BaseEntity {

	/**
	 * 阿里accessKeyId
	 */
	private String aliPushAccessKeyId;

	/**
	 * 阿里accessKeySecret
	 */
	private String aliPushAccessKeySecret;

	/**
	 * 阿里ios推送key
	 */
	private String aliPushIosAppKey;

	/**
	 * 阿里android推送key
	 */
	private String aliPushAndroidAppKey;

	/**
	 * 极光ios推送Secret
	 */
	private String jPushIosMasterSecret;

	/**
	 * 极光android推送Secret
	 */
	private String jPushAndroidMasterSecret;

	/**
	 * 极光ios推送key
	 */
	private String jPushIosAppKey;

	/**
	 * 极光android推送key
	 */
	private String jPushAndroidAppKey;
	
	/**
	 * 是否线上环境
	 */
	private Boolean apnsProduction;

}
