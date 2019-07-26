package com.lingdonge.auth.configuration;

import com.lingdonge.auth.configuration.properties.ShiroProperties;
import com.lingdonge.auth.jwt.StatelessAccessFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
@Slf4j
public class ShiroBaseAutoConfiguration {

    @Resource
    private ShiroProperties shiroProperties;

    /**
     * 身份验证过滤器
     *
     * @param securityManager
     * @return
     */
    @Bean("shiroFilter")
    @DependsOn("securityManager")
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {

        log.info("<<<<<<<<<<<<<< Shiro shiroFilter >>>>>>>>>>");

        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        if (StringUtils.isNotEmpty(shiroProperties.getLoginUrl())) {
            shiroFilter.setLoginUrl(shiroProperties.getLoginUrl());
        }

        if (StringUtils.isNotEmpty(shiroProperties.getUnauthorizeUrl())) {
            shiroFilter.setUnauthorizedUrl(shiroProperties.getUnauthorizeUrl());
        }

        // 拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        /*
         * 自定义url规则
         * http://shiro.apache.org/web.html#urls-
         * authc:所有url都必须认证通过才可以访问;
         * anon:所有url都都可以匿名访问
         *
         * 规则：1、相同url规则，后面定义的会覆盖前面定义的(执行的时候只执行最后一个)。
         * 2、两个url规则都可以匹配同一个url，只执行第一个
         * 3、注意是LinkedHashMap 保证有序
         * 允许用户匿名访问/login(登录接口)
         */
        filterChainDefinitionMap.put("/auth/**", "anon");// auth下面不需要验证，需要把所有注册登陆的逻辑放到auth下面
        filterChainDefinitionMap.put("/test/**", "anon");// test测试下面的都不用验证
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/v1/callback/**", "anon");//短信回调地址，不能为空
        filterChainDefinitionMap.put("/kaptcha.jpg", "anon");//图片验证码(kaptcha框架)
        filterChainDefinitionMap.put("/**", "jwt");

        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截 剩余的都需要认证
        filterChainDefinitionMap.put("/**", "authc");

//        filterChainDefinitionMap.put("/admin/**", "authc");
//        filterChainDefinitionMap.put("/user/**", "authc");

//        filterChainDefinitionMap.put("/**","anon");// 所有均不验证

        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);

        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
//        rm.setTemplate(shiroRedisTemplate);

//        filters.put("jcaptchaValidate",new JcaptchaValidateFilter());
        filters.put("jwt", new StatelessAccessFilter());// 添加过滤器
        shiroFilter.setFilters(filters);

        return shiroFilter;
    }


}
