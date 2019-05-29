package com.lingdonge.db.jdbc;

import javax.persistence.EntityManager;

public class EntityManagerUtil {

    private final EntityManager entityManager;

    /**
     * 构造函数
     *
     * @param entityManager
     */
    public EntityManagerUtil(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


//    private List<Object[]> groupByStudentAsSql() {
//        List<Object[]> list = entityManager
//                .createNativeQuery("select address,count(*) from t_student group by address")
//                .getResultList();
//
//        return list;
//    }
//
//    private List<Object[]> groupByStudentAsHql() {
//        List<Object[]> list = entityManager
//                .createQuery("select address,count(*) from Student group by address")
//                .getResultList();
//        return list;
//    }
//
//    private List<Object[]> groupByStudentAsSpecification() {
//        //根据地址分组查询，并且学生数量大于3的所有地址
//        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
////        Root<Student> root = query.from(Student.class);
////        query.multiselect(root.get("address"),builder.count(root.get("id")))
////                .groupBy(root.get("address")).having(builder.gt(builder.count(root.get("id")),3));
//
//        return entityManager.createQuery(query).getResultList();
//    }


}
