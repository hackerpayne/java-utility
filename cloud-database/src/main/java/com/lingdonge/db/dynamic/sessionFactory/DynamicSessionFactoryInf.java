package com.lingdonge.db.dynamic.sessionFactory;

import org.hibernate.SessionFactory;

/**
 * 定义扩展接口DynamicSessionFactoryInf继承SessionFactory
 */
public interface DynamicSessionFactoryInf extends SessionFactory {

    public SessionFactory getHibernateSessionFactory();
}