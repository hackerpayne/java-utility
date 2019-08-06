package com.lingdonge.db.configuration;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@AutoConfigureAfter(JpaProperties.class)
@EnableConfigurationProperties(JpaProperties.class)
@ComponentScan
//@EnableJpaRepositories(basePackageClasses = CustomerRepository.class, entityManagerFactoryRef = "customerEntityManager", transactionManagerRef = "customerTransactionManager")
@EnableTransactionManagement
public class HibernateAutoConfiguration {

    /**
     * 指定JPA属性；如Hibernate中指定是否显示SQL的“hibernate.show_sql”属性，对于jpaProperties设置的属性自动会放进jpaPropertyMap中；
     */
    @Autowired
    private JpaProperties jpaProperties;

    @Autowired(required = false)
    private PersistenceUnitManager persistenceUnitManager;

    /**
     * 用于指定一些高级特性，如事务管理，获取具有事务功能的连接对象等，目前Spring提供HibernateJpaDialect、OpenJpaDialect 、EclipseLinkJpaDialect、TopLinkJpaDialect、和DefaultJpaDialect实现，注意DefaultJpaDialect不提供任何功能，因此在使用特定实现厂商JPA实现时需要指定JpaDialect实现，如使用Hibernate就使用HibernateJpaDialect。当指定jpaVendorAdapter属性时可以不指定jpaDialect，会自动设置相应的JpaDialect实现；
     *
     * @return
     */
    @Bean
    public JpaDialect jpaDialect() {
        EclipseLinkJpaDialect dialect = new EclipseLinkJpaDialect();
        dialect.setLazyDatabaseTransaction(true);
        return dialect;
    }

    /**
     * 用于设置实现厂商JPA实现的特定属性，如设置Hibernate的是否自动生成DDL的属性generateDdl；这些属性是厂商特定的，因此最好在这里设置；目前Spring提供HibernateJpaVendorAdapter、OpenJpaVendorAdapter、EclipseLinkJpaVendorAdapter、TopLinkJpaVendorAdapter、OpenJpaVendorAdapter四个实现。
     * 其中最重要的属性是“database”，用来指定使用的数据库类型，从而能根据数据库类型来决定比如如何将数据库特定异常转换为Spring的一致性异常，目前支持如下数据库（DB2、DERBY、H2、HSQL、INFORMIX、MYSQL、ORACLE、POSTGRESQL、SQL_SERVER、SYBASE）。
     *
     * @return
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(jpaProperties.isShowSql());
        hibernateJpaVendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        hibernateJpaVendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        hibernateJpaVendorAdapter.setDatabase(jpaProperties.getDatabase());
        return hibernateJpaVendorAdapter;
    }

    /**
     * 解决EntityManagerFactory重复注入用EntityManagerFactory代替LocalContainerEntityManagerFactoryBean
     *
     * @return
     */
    @Bean(name = "entityManagerFactoryPrimary")
    @Primary
    public EntityManagerFactory primaryEntityManagerFactory(DataSource dataSource) {

        // 适用于所有环境的FactoryBean，能全面控制EntityManagerFactory配置,如指定Spring定义的DataSource等等。
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(jpaVendorAdapter());//配置jpa的适配器
        factory.setDataSource(dataSource);//数据源
        factory.setJpaDialect(this.jpaDialect());
        jpaProperties.getProperties().put("hibernate." + "ejb.naming_strategy_delegator", "none");
        jpaProperties.getProperties().put(org.hibernate.cfg.Environment.PHYSICAL_NAMING_STRATEGY, "cn.netconcepts.database.jpa.hibernate.CamelPhysicalNamingStrategyImpl");
        factory.setJpaPropertyMap(jpaProperties.getProperties());
        factory.setPackagesToScan("cn.netconcepts.usercenter.master.*.entity", "cn.netconcepts.usercenter.modular.*.*.entity");//设置实体类的扫描路径
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);//用于指定持久化实现厂商类；如Hibernate为org.hibernate.ejb.HibernatePersistence类；
        factory.setPersistenceUnitName("master");// 用于指定持久化单元名字
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    //	@Bean(name="entityManagerFactoryPrimary")
//    @Primary
//    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(EntityManagerFactoryBuilder builder, JpaProperties jpaProperties) {
//        return builder.dataSource(primaryDataSource())
//                    .properties(getVendorProperties(primaryDataSource(), jpaProperties))
//                    .packages("")
//                    .persistenceUnit("system")
//                    .build();
//    }

    /**
     * 配置事务支持
     *
     * @param entityManagerFactoryPrimary
     * @return
     */
    @Bean(name = "transactionManagerPrimary")
    @DependsOn("entityManagerFactoryPrimary")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactoryPrimary") LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary) {
        return new JpaTransactionManager(entityManagerFactoryPrimary.getObject());
    }

//    @Bean
//    @Primary
//    public JpaTransactionManager customerTransactionManager(final EntityManagerFactory factory) {
//        return new JpaTransactionManager(factory);
//    }

    /**
     * 获取Map格式的jpa配置文件
     *
     * @param dataSource
     * @param jpaProperties
     * @return
     */
    private Map<String, String> getVendorProperties(DataSource dataSource, JpaProperties jpaProperties) {
//        return jpaProperties.getHibernateProperties(dataSource);
        return jpaProperties.getProperties();
    }


}
