package com.lingdonge.db.fastsql;

import com.google.common.base.Splitter;
import top.fastsql.SQL;
import top.fastsql.dao.BaseDAO;
import top.fastsql.util.EntityRefelectUtils;
import top.fastsql.util.StringExtUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 拓展FastSQL，实现数据的OnDuplicate
 * 自己的DAO可以实现：
 * 定制一些通用方法
 * 设置多数据库支持
 * 设置BaseDAO中的触发器
 * 官方：https://github.com/fast-sql/FastSQL
 * <p>
 * 在BaseDAO下面可用的变量为：
 * Class<E> entityClass; //DAO对应的实体类
 * Class<ID> idClass;  //标识为@Id的主键类型
 * Logger log; //日志，可以在实现类中直接使用
 * String className; //实体类名
 * String tableName; //表名
 * Field idField;  //@Id对应的字段引用
 * String idColumnName; //表主键列名
 * namedParameterJdbcTemplate //jdbc模板
 *
 * @param <E>
 * @param <ID>
 */
public abstract class FastSqlDao<E, ID> extends BaseDAO<E, ID> {

    /**
     * 构造函数里面可以做一些操作
     */
    public FastSqlDao() {
        // 1.设置触发器开关
//        this.useBeforeInsert = true; //在插入前执行
//        this.useBeforeUpdate = true; //在更新前执行
    }

    //2.重写触发器相关方法

    /**
     * 插入前执行
     *
     * @param object
     */
    @Override
    protected void beforeInsert(E object) {
        EntityRefelectUtils.setFieldValue(object, "createdAt", LocalDateTime.now());
        EntityRefelectUtils.setFieldValue(object, "updatedAt", LocalDateTime.now());//在插入数据时自动写入createdAt,updatedAt
    }

    /**
     * 更新前执行
     *
     * @param object
     */
    @Override
    protected void beforeUpdate(E object) {
        EntityRefelectUtils.setFieldValue(object, "updatedAt", LocalDateTime.now());//在更新数据时自动更新updatedAt
    }

    /**
     * 增加一个逻辑删除功能，需要表结构里面存在字段：deleted，逻辑删除时，设置为1
     *
     * @param id 指定ID
     */
    public void logicDelete(ID id) {
        getSQL().getNamedParameterJdbcTemplate().getJdbcOperations().update("UPDATE " + this.tableName + " SET deleted = 1 where " + this.idColumnName + " = " + id);
    }

    /**
     * 使用insert on duplicate update来更新
     * 使用SQL为：INSERT INTO test (id,user_id,title,body) VALUES (:id,:userId,:title,:body) ON DUPLICATE KEY UPDATE title=:title,body=:body
     *
     * @param entity       要插入时使用的实体
     * @param updateFields 更新字段列表
     * @return
     */
    public int insertOnDupUpdate(E entity, String updateFields) {
        if (useBeforeInsert) {
            beforeInsert(entity);
        }

        final StringBuilder nameBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();

        fields.forEach(field -> {
            nameBuilder.append(",").append(StringExtUtils.camelToUnderline(field.getName()));
            valueBuilder.append(",:").append(field.getName());
        });

        final StringBuilder ondupBuilder = new StringBuilder();
        List<String> listStrs = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(updateFields);
        listStrs.forEach(updateField -> {
            ondupBuilder.append("," + StringExtUtils.camelToUnderline(updateField) + "=:" + updateField);
        });

        final SQL sql = getSQL()
                .INSERT_INTO(tableName, nameBuilder.deleteCharAt(0).toString())
                .VALUES(valueBuilder.deleteCharAt(0).toString())
                .append(" ON DUPLICATE KEY UPDATE ").append(ondupBuilder.deleteCharAt(0).toString())
                .beanParameter(entity);

        System.out.println(sql.build());

        final int count = sql.update();
        if (useAfterInsert) {
            afterInsert(entity, count);
        }
        return count;
//        return 0;
    }

    /**
     * 使用Insert on duplicate update来更新，但是只更新有值的字段
     * 如果没有值，会直接不处理
     *
     * @param entity
     * @param updateFields
     * @return
     */
    public int insertOnDupUpdateSelective(E entity, String updateFields) {
        if (useBeforeInsert) {
            beforeInsert(entity);
        }

        final StringBuilder nameBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();

        fields.stream()
                .filter(field -> EntityRefelectUtils.getFieldValue(entity, field) != null)
                .forEach(field -> {
                    nameBuilder.append(",").append(StringExtUtils.camelToUnderline(field.getName()));
                    valueBuilder.append(",:").append(field.getName());
                });

        final StringBuilder ondupBuilder = new StringBuilder();
        List<String> listStrs = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(updateFields);
        listStrs.forEach(updateField -> {
            ondupBuilder.append("," + StringExtUtils.camelToUnderline(updateField) + "=:" + updateField);
        });

        final SQL sql = getSQL()
                .INSERT_INTO(tableName, nameBuilder.deleteCharAt(0).toString())
                .VALUES(valueBuilder.deleteCharAt(0).toString())
                .append(" ON DUPLICATE KEY UPDATE ").append(ondupBuilder.deleteCharAt(0).toString())
                .beanParameter(entity);

        System.out.println(sql.build());

        final int count = sql.update();
        if (useAfterInsert) {
            afterInsert(entity, count);
        }
        return count;
//        return 0;
    }

}
