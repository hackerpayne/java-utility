package com.lingdonge.auth.service;


import com.lingdonge.auth.entity.ShiroInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * 用户相关的服务实现
 */
public interface CloudUserService {

    /**
     * 获取用户的基本信息，存到表里面便于进行对比
     *
     * @param account
     * @return
     */
    ShiroInfo getUser(String account);

    /**
     * 获取shiro登录用户的角色和权限，交给Service端去处理
     *
     * @param loginInfo 登录名
     * @return
     */
    SimpleAuthorizationInfo getSimpleAuthorizationInfoByStatelessRealm(String loginInfo);

}
