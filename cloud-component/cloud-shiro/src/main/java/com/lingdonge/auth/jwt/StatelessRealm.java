package com.lingdonge.auth.jwt;

import com.lingdonge.auth.entity.ShiroInfo;
import com.lingdonge.auth.service.CloudUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

/**
 * `realm`的用于处理用户是否合法的这一块，需要我们自己实现。
 */
@Slf4j
public class StatelessRealm extends AuthorizingRealm {

    @Resource
    private CloudUserService userService;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        //表示此Realm只支持JwtToken类型
        return token instanceof StatelessToken;
    }

    /**
     * 进行身份验证
     * 进入此处的情况：
     * 当调用Subject currentUser = SecurityUtils.getSubject();
     * currentUser.login(token);
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {

        // 获取Token，登陆相关的信息保存在里面
        StatelessToken token = (StatelessToken) auth;

        // 获取账号信息
        String account = token.getAccount();

        if (null == account || "".equals(account.trim())) {
            throw new UnknownAccountException("token profile 不存在");
        }

        // 从数据库读取用户信息
        ShiroInfo shiroInfo = userService.getUser(account);
        if (shiroInfo == null) {
            throw new UnknownAccountException("User didn't existed!");
        }


        // 密码错误
//        if (!JwtTokenUtil.verify(token, username, userBean.getPassword())) {
//            throw new AuthenticationException("Username or password error");
//        }

        /*
         * 获取权限信息:这里没有进行实现，
         * 请自行根据UserInfo,Role,Permission进行实现；
         * 获取之后可以在前端for循环显示所有链接;
         */
        //userInfo.setPermissions(userService.findPermissions(user));

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        return new SimpleAuthenticationInfo(token.getAccount(), token.getJwtToken(), getName());
    }

    /**
     * 进入授权阶段
     * 进入此处的情况：
     * 1、subject.hasRole(“admin”) 或 subject.isPermitted(“admin”)：自己去调用这个是否有什么角色或者是否有什么权限的时候；
     * 2、@RequiresRoles("admin") ：在方法上加注解的时候；
     * 3、[@auth.hasPermission name = "admin"][/@auth.hasPermission]：在页面上加shiro标签的时候，即进这个页面的时候扫描到有这个标签的时候。
     * <p>
     * 此处不做配置，把配置交给Service的实现层去主导操作
     * 权限信息.(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，
     * 调用clearCached方法；
     * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        //获取当前登录的用户名
        String loginInfo = (String) super.getAvailablePrincipal(principals);
        return userService.getSimpleAuthorizationInfoByStatelessRealm(loginInfo);
//
//        // 根据用户名，取出相关的角色和权限信息
//        String username = JwtTokenUtil.getUsername(principals.toString());
//        UserInfo user = userService.findByMobile(username);
//        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 添加角色
//        simpleAuthorizationInfo.addRole(user.getRole());
//        simpleAuthorizationInfo.addRoles(user.getRoleList());

        // 添加权限
//        Set<String> permission = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
//        simpleAuthorizationInfo.addStringPermissions(permission);
//        return simpleAuthorizationInfo;
    }


    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}
