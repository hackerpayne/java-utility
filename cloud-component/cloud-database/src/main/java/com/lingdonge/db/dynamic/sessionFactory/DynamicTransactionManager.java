package com.lingdonge.db.dynamic.sessionFactory;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.SessionFactoryUtils;

import javax.sql.DataSource;

/**
 * 定义DynamicTransactionManager继承HibernateTransactionManager
 */
public class DynamicTransactionManager extends HibernateTransactionManager {

    private static final long serialVersionUID = 1047039346475978451L;

    /**
     * 重写getDataSource方法，实现动态获取
     *
     * @return
     */
    @Override
    public DataSource getDataSource() {
        return SessionFactoryUtils.getDataSource(getSessionFactory());
    }

    /**
     * 重写getSessionFactory方法，实现动态获取SessionFactory
     *
     * @return
     */
    @Override
    public SessionFactory getSessionFactory() {
        DynamicSessionFactoryInf dynamicSessionFactory = (DynamicSessionFactoryInf) super
                .getSessionFactory();
        SessionFactory hibernateSessionFactory = dynamicSessionFactory
                .getHibernateSessionFactory();
        return hibernateSessionFactory;
    }

    /**
     * 重写afterPropertiesSet，跳过数据源的初始化等操作
     */
    @Override
    public void afterPropertiesSet() {
        return;
    }

}