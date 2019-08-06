package com.lingdonge.auth.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 用户名密码的载体,JWT无状态令牌
 * `StatelessToken`差不多就是`Shiro`用户名密码的载体。因为我们是前后端分离，服务器无需保存用户状态，所以不需要`RememberMe`这类功能，我们简单的实现下`AuthenticationToken`接口即可。因为`token`自己已经包含了用户名等信息，所以这里我就弄了一个字段。如果你喜欢钻研，可以看看官方的`UsernamePasswordToken`是如何实现的。
 */
public class StatelessToken implements AuthenticationToken {

    /**
     * 账号，可以是手机、用户名、邮箱等
     */
    private String account;//

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    /**
     * 生成的Token
     */
    private String jwtToken;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    private String salt;

    /**
     * 构造函数，传入账号和Token
     *
     * @param account
     * @param jwtToken
     */
    public StatelessToken(String account, String jwtToken) {
        this.account = account;
        this.jwtToken = jwtToken;
    }

    @Override
    public String getPrincipal() {
        return jwtToken;//更改为token
    }

    @Override
    public Object getCredentials() {
        return jwtToken;//更改为token
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

}
