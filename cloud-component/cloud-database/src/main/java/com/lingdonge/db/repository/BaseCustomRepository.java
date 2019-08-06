package com.lingdonge.db.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 建立公共的BaseDao通用类，需要取名为：BaseRepositoryImpl才能自动识别
 * 使用时，需要在主程序入口添加：@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
 * https://docs.spring.io/spring-data/jpa/docs/2.0.2.RELEASE/reference/html/#repositories.customize-base-repository
 * https://github.com/spring-projects/spring-data-examples/tree/master/jpa/example/src/main/java/example/springdata/jpa/customall
 * <p>
 * <p>
 * 因为Spring的问题，在2.0.0.m1里面才可以使用这个类库
 * https://jira.spring.io/browse/DATACMNS-1147
 *
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public class BaseCustomRepository<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    private final Class<T> entityClass;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 构造函数
     *
     * @param entityInformation
     * @param entityManager
     */
    public BaseCustomRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        // Keep the EntityManager around to used from the newly introduced methods.
        this.entityManager = entityManager;
        this.entityClass = entityInformation.getJavaType();
//        jdbcTemplate = SpringContextUtils.getBean(JdbcTemplate.class);
    }

    /**
     * 构造函数
     *
     * @param domainClass
     * @param entityManager
     */
    public BaseCustomRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.entityClass = domainClass;
//        jdbcTemplate = SpringContextUtils.getBean(JdbcTemplate.class);
    }

//    @Override
//    public boolean support(String modelType) {
//        return entityClass.getName().equals(modelType);
//    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
