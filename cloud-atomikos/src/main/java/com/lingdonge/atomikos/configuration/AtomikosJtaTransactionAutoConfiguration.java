package com.lingdonge.atomikos.configuration;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.UserTransaction;
import java.util.Properties;

/**
 * 分布式事物annotation
 */
@Configuration
@ComponentScan
@EnableTransactionManagement // 开启事务功能
//@ConditionalOnProperty(name = "transaction.enabled")// 必须开启transaction.set才会使用此配置
@Slf4j
public class AtomikosJtaTransactionAutoConfiguration {

    public AtomikosJtaTransactionAutoConfiguration() {
        log.info("加载[{}]类",this.getClass().getSimpleName());
    }

    /**
     * 用户事务
     *
     * @return
     * @throws Throwable
     */
    @Bean(name = "userTransaction")
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }

    /**
     * @return
     * @throws Throwable
     */
    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() throws Throwable {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setStartupTransactionService(false);
        userTransactionManager.setForceShutdown(false);

        return userTransactionManager;
    }

    /**
     * @return
     * @throws Throwable
     */
    @Bean(name = "transactionManagerPrimary")
    @DependsOn({"userTransaction", "atomikosTransactionManager"})
    public PlatformTransactionManager transactionManager() throws Throwable {
        UserTransaction userTransaction = userTransaction();

        JtaTransactionManager manager = new JtaTransactionManager(userTransaction, atomikosTransactionManager());
        manager.setAllowCustomIsolationLevels(true);
        log.info("<<<<<<<<<<<<<<< 分布式事物启动 >>>>>>>>>>>>>>>>>>");
        return manager;
    }

    /**
     * @param platformTransactionManager
     * @return
     */
    @Bean(name = "transactionInterceptor")
    public TransactionInterceptor transactionInterceptor(PlatformTransactionManager platformTransactionManager) {
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        // 事物管理器
        transactionInterceptor.setTransactionManager(platformTransactionManager);
        Properties transactionAttributes = new Properties();

        // multithreading
        transactionAttributes.setProperty("multithreading*", "PROPAGATION_REQUIRED,-Throwable");

        // 新增
        transactionAttributes.setProperty("insert*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("add*", "PROPAGATION_REQUIRED,-Throwable");
        transactionAttributes.setProperty("save*", "PROPAGATION_REQUIRED,-Throwable");
        // 修改

        transactionAttributes.setProperty("update*", "PROPAGATION_REQUIRED,-Throwable");
        // 删除
        transactionAttributes.setProperty("delete*", "PROPAGATION_REQUIRED,-Throwable");
        // 删除
        transactionAttributes.setProperty("del*", "PROPAGATION_REQUIRED,-Throwable");
        // 查询
        transactionAttributes.setProperty("*", "PROPAGATION_REQUIRED,-Throwable,readOnly");

//        // 新增
//        transactionAttributes.setProperty("insert*", "PROPAGATION_REQUIRED,-Exception");
//        // 修改
//        transactionAttributes.setProperty("update*", "PROPAGATION_REQUIRED,-Exception");
//        // 删除
//        transactionAttributes.setProperty("delete*", "PROPAGATION_REQUIRED,-Exception");
//        // 查询
//        transactionAttributes.setProperty("select*", "PROPAGATION_REQUIRED,-Exception,readOnly");
//        // 统计
//        transactionAttributes.setProperty("count*", "PROPAGATION_REQUIRED,-Exception,readOnly");

        transactionInterceptor.setTransactionAttributes(transactionAttributes);
        return transactionInterceptor;

    }

    /**
     * 代理到ServiceImpl的Bean
     *
     * @return
     */
    @Bean
    public BeanNameAutoProxyCreator transactionAutoProxy() {
        BeanNameAutoProxyCreator transactionAutoProxy = new BeanNameAutoProxyCreator();
        transactionAutoProxy.setProxyTargetClass(true);
        transactionAutoProxy.setBeanNames("*ServiceImpl");
        transactionAutoProxy.setInterceptorNames("transactionInterceptor");
        return transactionAutoProxy;
    }
}
