package com.lingdonge.db.bean.dao;

import com.lingdonge.db.bean.entity.ModelTest;
import com.lingdonge.db.fastsql.FastSqlDao;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

/**
 * 可以直接在DAO里面加上处理各种数据的逻辑
 * 在继承BaseDAO的类中你可以你可以直接调用 getSQL() 来获取一个SQL的实例
 */
public class TestFastSqlDao extends FastSqlDao<ModelTest, String> {

    /**
     * 条件过滤器使用
     *
     * @return
     */
    @Test
    public int countByName() {
        return countWhere("photo_name=:name", new MapSqlParameterSource().addValue("name", "物品照片"));
    }

    /**
     * 查询姓李的同学列表
     */
    @Test
    public List<ModelTest> queryListByName() {
        return getSQL().SELECT("*").FROM(this.tableName)
                .WHERE("name").LIKE("'李%'")
                .queryList(ModelTest.class);//查询列表
    }

    /**
     * 根据旧名字修改学生姓名
     */
    @Test
    public int updateName(String oldName, String newName) {
        return getSQL().UPDATE(this.tableName).SET("name = '" + newName + "'").WHERE("name").eqByType(oldName).update();
    }

}
