package com.lingdonge.auth.jwt;

import com.lingdonge.crypto.encrypt.HmacSHA256Utils;
import com.lingdonge.spring.bean.token.JwtUserInfo;
import com.lingdonge.spring.token.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * 自定义密码校验规则，可以不使用，可以进行账号密码验校，也可以设置多次重试之后的处理策略
 * 如果使用的话，在登陆的时候，会生成Token并返回
 */
@Slf4j
public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {

    private JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken statelessToken, AuthenticationInfo info) {

        StatelessToken token = (StatelessToken) statelessToken;//获取登录相关信息
        Object accountCredentials = getCredentials(info);

        String pwd = null;
        try {
            pwd = HmacSHA256Utils.encrypt(token.getSalt(), token.getPassword());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (equals(pwd, accountCredentials)) {//密码匹对成功生成token 30分钟有效

            JwtUserInfo jwtUserInfo = new JwtUserInfo();
            jwtUserInfo.setUserId(token.getAccount());
            jwtUserInfo.setSubject(token.getSalt());

            token.setJwtToken(jwtTokenUtil.generateToken(jwtUserInfo));
            return true;
        }

        return false;
    }
}
