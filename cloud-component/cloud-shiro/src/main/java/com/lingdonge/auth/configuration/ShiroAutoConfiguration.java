package com.lingdonge.auth.configuration;

import com.lingdonge.auth.realm.MyRealm;
import com.lingdonge.spring.configuration.properties.JwtProperties;
import com.lingdonge.spring.token.JwtTokenUtil;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(JwtProperties.class)
@Configuration
public class ShiroAutoConfiguration {

    @Autowired
    private JwtProperties jwtProperties;

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager(myRealm());
        return defaultWebSecurityManager;
    }

    @Bean
    public MyRealm myRealm() {
        MyRealm myRealm = new MyRealm();
        return myRealm;
    }

    @Bean
    public JwtTokenUtil jwtTokenHelper() {
        return new JwtTokenUtil(jwtProperties);
    }

}
