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

//    @Bean
//    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
//        //以下是过滤链，按顺序过滤，所以/**需要放最后
//        //开放的静态资源
//        filterChainDefinitionMap.put("/favicon.ico", "anon");//网站图标
//        filterChainDefinitionMap.put("/**", "authc");
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
//        return shiroFilterFactoryBean;
//    }

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
