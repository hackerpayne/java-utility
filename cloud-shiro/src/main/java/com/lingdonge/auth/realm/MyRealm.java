package com.lingdonge.auth.realm;

import com.lingdonge.auth.entity.ShiroInfo;
import com.lingdonge.auth.service.CloudUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

/**
 * 自定义的CustomRealm继承AuthorizingRealm。并且重写父类中的doGetAuthorizationInfo（权限相关）、doGetAuthenticationInfo（身份认证）这两个方法。
 */
@Slf4j
public class MyRealm extends AuthorizingRealm {

    @Resource
    private CloudUserService userService;

    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //从凭证中获得用户名
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        //根据用户名查询用户对象
        ShiroInfo user = userService.getUser(username);
//        //查询用户拥有的角色
//        List<Role> list = roleService.findByUserId(user.getId());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        for (Role role : list) {
//            //赋予用户角色
//            info.addStringPermission(role.getRole());
//        }
        return info;
    }

    /**
     * 认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //获得当前用户的用户名
        String username = (String) authenticationToken.getPrincipal();

        //从数据库中根据用户名查找用户
        ShiroInfo user = userService.getUser(username);
        if (userService.getUser(username) == null) {
            throw new UnknownAccountException("用户不存在。");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getAccount(), user.getPassword(), getName());
        return info;
    }

}
