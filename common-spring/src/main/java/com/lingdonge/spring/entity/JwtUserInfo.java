package com.lingdonge.spring.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Jwt里面收集的用户信息列表
 */
@Data
public class JwtUserInfo implements Serializable {

    private static final long serialVersionUID = -895875540581785581L;

    /**
     * 令牌id
     */
    private String tokenId;

    /**
     * 客户标识(用户id)
     */
    private String subject;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 签发者(JWT令牌此项有值)
     */
    private String issuer;

    /**
     * 签发时间
     */
    private Date issuedAt;

    /**
     * 接收方(JWT令牌此项有值)
     */
    private String audience;

    /**
     * 访问主张-角色(JWT令牌此项有值)
     */
    private String roles;

    /**
     * 访问主张-资源(JWT令牌此项有值)
     */
    private String perms;

    /**
     * 客户地址
     */
    private String host;

    /**
     * Token过期时间，单位是秒
     * 默认30分钟
     */
    private Long expires = 30 * 60L;

}
