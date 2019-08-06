package com.lingdonge.db.jdbc;

import com.lingdonge.db.bean.entity.ModelTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class JdbcTemplateTest {

    @Resource
    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcTemplateTest() {
//        jdbcTemplate.setResultsMapCaseInsensitive(true);//ResultMap是否大小写敏感
    }

    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void updateOne() {
        String sql = "update user set name=?,deptid=? where id=?";
        jdbcTemplate.update(sql, new Object[]{"zhh", 5, 51});
    }

    public Integer getCount() {
        int rowCount = jdbcTemplate.queryForObject("select count(*) from t_actor", Integer.class);
        return rowCount;
    }

    public Integer getCountBy() {
        int count = jdbcTemplate.queryForObject("select count(*) from t_actor where first_name = ?", Integer.class, "Joe");

        String sql = "insert into user (name,deptid) values (?,?)";
        count = jdbcTemplate.update(sql, new Object[]{"caoyc", 3});

        return count;
    }

    public void deleteOne() {
        String sql = "delete from user where id=?";
        jdbcTemplate.update(sql, 51);
    }

    public void getOneToBean() {
        String sql = "select id,name,deptid from user where id=?";

        RowMapper<ModelTest> rowMapper = new BeanPropertyRowMapper<ModelTest>(ModelTest.class);
        ModelTest user = jdbcTemplate.queryForObject(sql, rowMapper, 52);
        System.out.println(user);
    }

    public ModelTest getOne() {

        ModelTest testEntity = jdbcTemplate.queryForObject("select first_name, last_name from t_actor where id = ?",
                new Object[]{1212L},
                new RowMapper<ModelTest>() {
                    @Override
                    public ModelTest mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ModelTest actor = new ModelTest();
//                        actor.setCreatedAt(rs.getString("first_name"));
                        actor.setTitle(rs.getString("last_name"));
                        return actor;
                    }
                });

        return testEntity;
    }

    public ModelTest queryOne() {
        final ModelTest user = new ModelTest();
        jdbcTemplate.query("SELECT id,name,.. FROM tblname WHERE id=1",
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
//                        user.setTitle(rs.getLong(1));
                        user.setTitle(rs.getString(2));
                    }
                }
        );

        return user;

    }

    public void queryToMap() {
        List uNames = jdbcTemplate.queryForList("SELECT name FROM tblname WHERE id>?", new Integer[]{5}, String.class);
        List<Map<String, Object>> uMapList = jdbcTemplate.queryForList("SELECT id, name FROM tblname WHERE id>?", new Integer[]{5});
        for (Map<String, Object> uMap : uMapList) {
            Integer id = (Integer) uMap.get("id");
            String name = (String) uMap.get("name");
        }

    }

    public List<ModelTest> getList() {

        List<ModelTest> actors = jdbcTemplate.query("select first_name, last_name from t_actor",
                new RowMapper<ModelTest>() {
                    @Override
                    public ModelTest mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ModelTest actor = new ModelTest();
                        actor.setTitle(rs.getString("first_name"));
                        return actor;
                    }
                });
        return actors;
    }

    public void getListToBean() {
        String sql = "select id,name,deptid from user";

        RowMapper<ModelTest> rowMapper = new BeanPropertyRowMapper<ModelTest>(ModelTest.class);
        List<ModelTest> users = jdbcTemplate.query(sql, rowMapper);
        for (ModelTest user : users) {
            System.out.println(user);
        }
    }

    public List<ModelTest> findAllActors() {
        return jdbcTemplate.query("select first_name, last_name from t_actor", new ActorMapper());
    }

    private static final class ActorMapper implements RowMapper<ModelTest> {

        @Override
        public ModelTest mapRow(ResultSet rs, int rowNum) throws SQLException {
            ModelTest actor = new ModelTest();
            actor.setTitle(rs.getString("first_name"));
            return actor;
        }
    }


    public void testUpdate() {
        jdbcTemplate.update("insert into t_actor (first_name, last_name) values (?, ?)", "Leonor", "Watling");
        jdbcTemplate.update("update t_actor set last_name = ? where id = ?", "Banjo", 5276L);
        jdbcTemplate.update("delete from actor where id = ?", Long.valueOf(1));
    }

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    public Integer insertJdbcTemplateBatchCaseAnalyze(final List<ModelTest> list) {
        final String batch = new String(System.currentTimeMillis() + "");
        String sql = "INSERT INTO case_analyze (user_id, case_category_type) VALUES (?, ?);";

        jdbcTemplate.batchUpdate(sql, list, 500, new ParameterizedPreparedStatementSetter<ModelTest>() {
            @Override
            public void setValues(PreparedStatement ps, ModelTest argument) throws SQLException {
                ps.setObject(1, argument.getTitle());
            }
        });

        return list.size();
    }

    public int[] batchUpdate(final List<ModelTest> actors) {
        int[] updateCounts = jdbcTemplate.batchUpdate("update t_actor set first_name = ?, last_name = ? where id = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, actors.get(i).getTitle());
//                        ps.setString(2, actors.get(i).getLastName());
                        ps.setLong(3, actors.get(i).getId().longValue());
                    }

                    @Override
                    public int getBatchSize() {
                        return actors.size();
                    }
                });
        return updateCounts;
    }


    public int[] batchUpdate2(final List<ModelTest> actors) {
        List<Object[]> batch = new ArrayList<Object[]>();
        for (ModelTest actor : actors) {
            Object[] values = new Object[]{actor.getTitle(), actor.getId()};
            batch.add(values);
        }
        int[] updateCounts = jdbcTemplate.batchUpdate("update t_actor set first_name = ?, last_name = ? where id = ?", batch);
        return updateCounts;
    }

    public int[][] batchUpdate(final Collection<ModelTest> actors) {
        int[][] updateCounts = jdbcTemplate.batchUpdate("update t_actor set first_name = ?, last_name = ? where id = ?", actors, 100,
                new ParameterizedPreparedStatementSetter<ModelTest>() {
                    @Override
                    public void setValues(PreparedStatement ps, ModelTest argument) throws SQLException {
                        ps.setString(1, argument.getTitle());
                        ps.setLong(3, argument.getId().longValue());
                    }
                });
        return updateCounts;
    }


    public int[] batchUpdateUseNamed(final List<ModelTest> actors) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(actors.toArray());
        int[] updateCounts = namedParameterJdbcTemplate.batchUpdate("update t_actor set first_name = :firstName, last_name = :lastName where id = :id", batch);
        return updateCounts;
    }

    public void testExecute() {
        this.jdbcTemplate.execute("create table mytable (id integer, name varchar(100))");
    }

    public void insert() {
        final String INSERT_SQL = "insert into my_test (name) values(?)";
        final String name = "Rob";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, name);
                        return ps;
                    }
                },
                keyHolder);

        System.out.println(keyHolder.getKey()); //now contains the generated key

    }

    public void jdbcTransTest(List<String> listDatas) {
        PlatformTransactionManager tm = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        TransactionStatus status = null;
        try {
            //null 默认事务属性配置DefaultTransactionDefinition
            status = tm.getTransaction(null);
            for (final String wd : listDatas) {
                try {
                    jdbcTemplate.update("", new PreparedStatementSetter() {

                                @Override
                                public void setValues(PreparedStatement pstate)
                                        throws SQLException {
                                    pstate.setString(1, wd);
//                                    pstate.setTimestamp(2, new Timestamp(new Date().getTime()));
                                }
                            }
                    );

                } catch (DataAccessException e) {
                    e.printStackTrace();
                    //tm.rollback(status);
                }
            } // end for
        } finally {
            tm.commit(status);
        }

    }
}
