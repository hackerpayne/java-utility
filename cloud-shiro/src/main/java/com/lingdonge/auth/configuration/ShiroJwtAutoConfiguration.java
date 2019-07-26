package com.lingdonge.auth.configuration;

import com.lingdonge.auth.jwt.StatelessAccessFilter;
import com.lingdonge.auth.jwt.StatelessDefaultSubjectFactory;
import com.lingdonge.auth.jwt.StatelessRealm;
import com.lingdonge.spring.configuration.properties.JwtProperties;
import com.lingdonge.spring.token.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置
 */
@Configuration
//@EnableConfigurationProperties(ShiroProperties.class) // 开启指定类的配置
@EnableConfigurationProperties(JwtProperties.class) // 开启指定类的配置
@ConditionalOnProperty(name = "auth.enabled")// 必须开启shiro.set才会使用此配置
@Slf4j
public class ShiroJwtAutoConfiguration {

    /**
     * 解決spring和shiro整合时候事务失效，单独把Realm进行注入
     *
     * @param event
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

        log.info("<<<<<<<<<<< 执行Ream注入 >>>>>>>>>>>>>>");

        ApplicationContext context = event.getApplicationContext();
        DefaultWebSecurityManager manager = (DefaultWebSecurityManager) context.getBean("securityManager");
        StatelessRealm realm = (StatelessRealm) context.getBean("statelessRealm");
//        realm.setCredentialsMatcher(new CustomCredentialsMatcher());
        manager.setRealm(realm);
    }

    /**
     * 用户认证以及授权
     *
     * @return
     */
    @Bean("statelessRealm")
    public StatelessRealm getStatelessRealm() {

        log.info("<<<<<<<<<<< 执行Ream Bean加载 >>>>>>>>>>>>>>");

        StatelessRealm statelessRealm = new StatelessRealm();
//        statelessRealm.setCacheManager(getRedisCacheManager());
        statelessRealm.setCachingEnabled(false);
//        statelessRealm.setCredentialsMatcher(new CustomCredentialsMatcher());//自定义密码校验，可以不使用

        return statelessRealm;
    }

    /**
     * SubJect工厂管理器
     *
     * @return
     */
    @Bean("subjectFactory")
    public StatelessDefaultSubjectFactory getStatelessDefaultSubjectFactory() {
        return new StatelessDefaultSubjectFactory();
    }

    /**
     * 会话管理器关闭shiro自带的sessionStorage，详情见文档，或者使用下面的defaultSessionManager功能进行管理，2种方式均可
     * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
     *
     * @return
     */
    public DefaultSessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        // 关闭session定时检查，通过setSessionValidationSchedulerEnabled禁用掉会话调度器
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }

    /**
     * subject(代理特定的一个用户的所有权限相关操作，登陆退出、授权，获取session等)的管理组件，
     * 仅提供了save和delete方法。用于保存subject的状态，方便与以后可以重建subject
     *
     * @return
     */
    @Bean
    public SubjectDAO getSubjectDAO() {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        return subjectDAO;
    }

    /**
     * 安全管理器
     *
     * @return
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager() {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

//        securityManager.setRealm(getStatelessRealm());//导致事务失败

        // 替换默认的DefaultSubjectFactory，用于关闭session功能
        securityManager.setSubjectFactory(getStatelessDefaultSubjectFactory());
        securityManager.setSessionManager(sessionManager());

        // 关闭session存储，禁用Session作为存储策略的实现，但它没有完全地禁用Session所以需要配合SubjectFactory中的context.setSessionCreationEnabled(false)
        //((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO)securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);

        // 用户授权/认证信息Cache, 后期可采用EhCache缓存
        // securityManager.setCacheManager(cacheManager());

        // 采用redis缓存，使用另外一个配置
//        securityManager.setCacheManager(getRedisCacheManager());
        securityManager.setSubjectDAO(getSubjectDAO());

        // 注册manager,方便全局获取
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }


//    /**
//     * 凭证匹配器
//     * @return
//     */
//    @Bean
//    public CredentialsMatcher credentialsMatcher(){
//        HashedCredentialsMatcher hashedCredentialsMatcher =new HashedCredentialsMatcher();
//        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
//        hashedCredentialsMatcher.setHashIterations(1024);
//        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
//        return hashedCredentialsMatcher;
//    }

//    /**
//     * 用户授权信息缓存
//     * @return
//     */
//    @Bean
//    public CacheManager cacheManager() {
//        // EhCacheManager cacheManager = new EhCacheManager();
//        // cacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
//        return new MemoryConstrainedCacheManager();
//    }

    /**
     * Shiro生命周期处理器
     *
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    /**
     * 开启Shiro注解(如@RequiresRoles,@RequiresPermissions)
     *
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

//    /**
//     * cookie对象;
//     * @return
//     * */
//    @Bean
//    public SimpleCookie rememberMeCookie(){
//        //System.out.println("ShiroConfiguration.rememberMeCookie()");
//        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
//        simpleCookie.setMaxAge(259200);
//        return simpleCookie;
//    }
//    /**
//     * cookie管理对象;
//     * @return
//     */
//    @Bean
//    public CookieRememberMeManager rememberMeManager(){
//        //System.out.println("ShiroConfiguration.rememberMeManager()");
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        return cookieRememberMeManager;
//    }


    /**
     * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        securityManager.setSessionManager(sessionManager());
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 建立一个实例
     *
     * @return
     */
    @Bean
    public JwtTokenUtil jwtTokenHelper() {
        return new JwtTokenUtil(jwtProperties);
    }


}
