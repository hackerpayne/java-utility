package com.lingdonge.db.fastsql;

import com.google.common.collect.Lists;
import com.lingdonge.db.bean.dao.TestFastSqlDao;
import com.lingdonge.db.bean.entity.ModelTest;
import com.mysql.jdbc.Driver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import top.fastsql.SQL;
import top.fastsql.SQLFactory;
import top.fastsql.config.DataSourceType;
import top.fastsql.util.FastSqlUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
public class FastSqlDaoTest {

    /**
     * 手工创建SQL工厂
     *
     * @return
     * @throws SQLException
     */
    public SQLFactory buildSqlFactory() {
        //创建任意DataSource对象（这里使用了spring自带的数据源SimpleDriverDataSource）
        DataSource dataSource = null;
        try {
            dataSource = new SimpleDriverDataSource(
                    new Driver(), "jdbc:mysql://127.0.0.1:3306/test?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false",
                    "root", "123456");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //创建SqlFactory
        SQLFactory sqlFactory = new SQLFactory();
        sqlFactory.setDataSource(dataSource);
        sqlFactory.setDataSourceType(DataSourceType.MY_SQL);
        return sqlFactory;
    }

    /**
     * 生成一个基础的Dao
     *
     * @return
     */
    public TestFastSqlDao buildDao() {
        SQLFactory sqlFactory = buildSqlFactory();

        TestFastSqlDao testFastSQLDao = new TestFastSqlDao();
        testFastSQLDao.setSqlFactory(sqlFactory);

        return testFastSQLDao;
    }

    public SQL buildSql() {
        SQL sql = buildSqlFactory().createSQL();
        return sql;
    }

    /**
     * Dao插入测试
     *
     * @throws SQLException
     */
    @Test
    public void testInsert() {
        TestFastSqlDao testFastSQLDao = buildDao();

        ModelTest modelTest = new ModelTest();
        modelTest.setTitle("测试标题");
        modelTest.setUserId(1);
        modelTest.setBody("hahah");
        Integer insert = testFastSQLDao.insert(modelTest);
        log.info("testInsert测试插入结果：{}", insert);
    }

    @Test
    public void testInsertUpdate() {
        TestFastSqlDao testFastSQLDao = buildDao();

        ModelTest modelTest = new ModelTest();
        modelTest.setTitle("测试标题");
        modelTest.setUserId(1);
//        modelTest.setBody("hahah");

        Integer insert = testFastSQLDao.insertOnDupUpdateSelective(modelTest, "title,body");
        log.info("testInsertUpdate测试插入结果：{}", insert);
    }

    @Test
    public void crateSQL() {

        ModelTest modelTest = buildSql().SELECT("*").FROM("test").WHERE("id=1").queryOne(ModelTest.class);
        log.info("crateSQL查询结果为：{}", modelTest);
    }


    /**
     * 测试byType()
     */
    @Test
    public void testByType() {
        buildSqlFactory().createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("age").lt().byType(10)
                .AND("name").eq().byType("小明")
                .build();
    }


    /**
     * 测试IN语句
     */
    @Test
    public void testIN() {
        buildSqlFactory().createSQL()
                .SELECT("name", "age")
                .FROM("student")
                .WHERE("name").IN(new Object[]{"小红", "小明"})
                .build();

        buildSqlFactory().createSQL()
                .SELECT("name", "age")
                .FROM("student")
//                .WHERE("name").IN("小红", "小明")
                .build();
    }

    /**
     * 测试page=0的情况
     */
    @Test
    public void testPage0() {
        System.out.println(buildSql().SELECT("id").FROM("sys_dict").queryPage(0, 1, String.class));
    }

    /**
     * 测试PerPage=0的情况
     */
    @Test
    public void testPerPage0() {
        System.out.println(buildSql().SELECT("id").FROM("sys_dict").queryPage(1, 0, String.class));
    }

    /**
     * 获取LIKE通配符
     */
    @Test
    public void test() {
        FastSqlUtils.bothWildcard("李"); // => %李%
        FastSqlUtils.leftWildcard("李");  // => %李
        FastSqlUtils.rightWildcard("李"); // => 李%
    }

    /**
     * 获取sql的IN列表
     * FastSQLUtils.getInClause(Collection<?> collection),会根据Collection的类型自动判断使用什么样的分隔符:
     */
    @Test
    public void in() {

        FastSqlUtils.getInClause(Lists.newArrayList(1, 23, 4, 15));
//生成=>(1,23,4,15)

        FastSqlUtils.getInClause(Lists.newArrayList("dog", "people", "food", "apple"));
//生成=> ('dog','people','food','apple')

    }


}
