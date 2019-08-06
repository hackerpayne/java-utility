package com.lingdonge.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * 一个基类，不用每次继承那么多类
 * https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/#repositories.customize-base-repository
 * https://zhuanlan.zhihu.com/p/20919236
 * 使用时：public interface TrsRepository extends BaseRepository<TrsEntity, Long>
 *
 * 常用的接口实现有：
 * CrudRepository： 继承 Repository，实现了一组 CRUD 相关的方法(基本的增删改查的方法)
 * PagingAndSortingRepository： 继承 CrudRepository，实现了一组分页排序相关的方法
 * JpaRepository： 继承 PagingAndSortingRepository，实现一组 JPA 规范相关的方法
 *
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean // Jpa在启动时就不会去实例化BaseRepository这个接口
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    // 表示该Repository的领域对象是否为modelType类型
//    boolean support(String modelType);

}