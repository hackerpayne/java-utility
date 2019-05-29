package com.lingdonge.auth.jwt;

import com.lingdonge.auth.constant.ConstantDef;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.apache.shiro.mgt.SecurityManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ﻿Shiro过滤器工厂类，具体的实现类是：ShiroFilterFactoryBean，此实现类是依赖于SecurityManager安全管理器的。
 */
@Slf4j
public class StatelessShiroFilterFactoryBean extends ShiroFilterFactoryBean {

    // 对ShiroFilter来说，需要直接忽略的请求

    // 将filterMap放入redis,以支持热部署和集群
    private RedisTemplate<Serializable, Object> shiroRedisTemplate;

    public StatelessShiroFilterFactoryBean() {
        shiroRedisTemplate.opsForValue().set(ConstantDef.RESOURCE_DEFAULT_URL_LIST, new HashMap<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getFilterChainDefinitionMap() {
        return (Map<String, String>) shiroRedisTemplate.opsForValue().get(ConstantDef.RESOURCE_DEFAULT_URL_LIST);
    }

    @Override
    public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
        shiroRedisTemplate.opsForValue().set(ConstantDef.RESOURCE_DEFAULT_URL_LIST, filterChainDefinitionMap);
    }


    @Override
    protected AbstractShiroFilter createInstance() throws Exception {

        SecurityManager securityManager = getSecurityManager();
        if (securityManager == null) {
            String msg = "SecurityManager property must be set.";
            throw new BeanInitializationException(msg);
        }

        if (!(securityManager instanceof WebSecurityManager)) {
            String msg = "The security manager does not implement the WebSecurityManager interface.";
            throw new BeanInitializationException(msg);
        }

        FilterChainManager manager = createFilterChainManager();
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);
        return new MSpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
    }

    private final class MSpringShiroFilter extends AbstractShiroFilter {

        protected MSpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
            super();
            if (webSecurityManager == null) {
                throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
            }
            setSecurityManager(webSecurityManager);
            if (resolver != null) {
                setFilterChainResolver(resolver);
            }
        }

        @Override
        protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse,
                                        FilterChain chain) throws ServletException, IOException {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String str = request.getRequestURI().toLowerCase();
            boolean flag = true;
            int idx;
            SecurityUtils.getSubject().getSession(false);
            if ((idx = str.lastIndexOf('.')) > 0) {
                str = str.substring(idx);
                if (getIgnoreExt().contains(str.toLowerCase())) {
                    flag = false;
                }
            }
            if (flag) {
                super.doFilterInternal(servletRequest, servletResponse, chain);
            } else {
                chain.doFilter(servletRequest, servletResponse);
            }
        }

        @SuppressWarnings("unchecked")
        private Set<String> getIgnoreExt() {
            return (Set<String>) shiroRedisTemplate.opsForValue().get(ConstantDef.RESOURCE_FILTER_SET);
        }

    }


}
