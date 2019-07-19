package com.lingdonge.db.jdbc;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.lingdonge.core.bean.base.BaseEntity;
import com.lingdonge.core.bean.base.ModelPair;
import com.lingdonge.core.page.PageUtil;
import com.lingdonge.core.reflect.NamingUtil;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.db.util.SqlBuilder;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 不带实体的JdbcTemplate操作
 */
@Getter
@Setter
@Slf4j
public class JdbcTemplateUtil extends BaseEntity {

    private String tableName;

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateUtil() {

    }

    /**
     * @param tableName
     */
    public JdbcTemplateUtil(String tableName) {
        this(null, tableName);
    }

    /**
     * @param jdbcTemplate
     */
    public JdbcTemplateUtil(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, "");
    }

    /**
     * @param jdbcTemplate
     * @param tableName
     */
    public JdbcTemplateUtil(JdbcTemplate jdbcTemplate, String tableName) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = SqlBuilder.buildTableName(tableName.trim());
    }

    /**
     * 集合查询-限制返回结果的条数
     *
     * @param fields    返回的字段
     * @param tableName 表名
     * @param maxresult 返回条数 0返回所有
     * @param where     where条件
     * @param params    where参数值
     * @param groupby   group条件
     * @param orderby   order条件
     * @param rowMapper Row映射关系
     * @param <T>
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public <T> List<T> findByList(String[] fields, String tableName, int maxresult, String where,
                                  List<Object> params, String
                                          groupby, LinkedHashMap<String, String> orderby, RowMapper<T> rowMapper) throws Exception {
        StringBuffer sql = new StringBuffer("select ");
        // 如果fields为Null，查所有字段
        if (fields != null && fields.length > 0) {
            sql.append(SqlBuilder.buildFields(fields));
        } else {
            sql.append("*");
        }

        sql.append(" from ").append(tableName)
                .append(StringUtils.isEmpty(where) ? "" : " where " + where).append(" ")
                .append(StringUtils.isEmpty(groupby) ? "" : groupby)
                .append(SqlBuilder.buildOrderby(orderby));
        if (params == null) {
            params = new ArrayList<Object>();
        }
        if (maxresult > 0) {
            sql.append(" limit 1,?;");
            params.add(maxresult);
        }
        return getJdbcTemplate().query(sql.toString(), params.toArray(), rowMapper);
    }

    /**
     * 新增数据
     *
     * @param map 字段和Value值，放入Map中入库
     */
    public Integer insert(Map<String, Object> map) {
        Pair<String, Object[]> pair = SqlBuilder.buildInsert(tableName, map);
        return getJdbcTemplate().update(pair.getKey(), pair.getValue());
    }

    /**
     * 批量插入数据
     *
     * @param listData
     */
    public int[] batchInsert(final List<Map<String, Object>> listData) {
        Pair<String, List<Object[]>> pair = SqlBuilder.buildBatchInsert(tableName, listData);
        return jdbcTemplate.batchUpdate(pair.getKey(), pair.getValue());
    }

    /**
     * 批量插入数据，不考虑是否重复等因素
     *
     * @param tableName 表名
     * @param fields    字段列表
     * @param list      数据列表
     * @throws Exception
     */
    public void batchInsert(String tableName, final String[] fields, final List<Map<String, Object>> list) throws Exception {
        batchInsert(tableName, fields, list, false);
    }

    /**
     * 批量新增，默认不使用insert ignore的形式插入数据
     *
     * @param tableName
     * @param fields
     * @param list
     * @param ignore    是否重复，如果重复的话，会使用insert ignore忽略插入的形式进行数据插入
     * @throws Exception
     */
    public void batchInsert(String tableName, final String[] fields, final List<Map<String, Object>> list, boolean ignore) {

        StringBuffer sql = new StringBuffer("insert ")
                .append(ignore ? " ignore into " : " into ")
                .append(tableName).append(" (").append(SqlBuilder.buildFields(fields)).append(") values (")
                .append(SqlBuilder.buildQuestion(fields)).append(");");

        getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                for (int j = 0, len = fields.length; j < len; j++) {
                    ps.setObject(j + 1, list.get(i).get(fields[j]));
                }
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    /**
     * 批量新增-当插入重复时更新数据
     *
     * @param tableName    表名
     * @param fields       新增字段列表
     * @param updateFields 更新字段列表
     * @param list
     * @throws Exception
     */
    public void batchInsertWithUpdate(String tableName, final String[] fields, final String[] updateFields,
                                      final List<Map<String, Object>> list) throws Exception {

        StringBuffer sql = new StringBuffer("insert into ")
                .append(tableName).append(" (")
                .append(SqlBuilder.buildFields(fields))
                .append(") values (")
                .append(SqlBuilder.buildQuestion(fields))
                .append(") ");

        if (updateFields != null && updateFields.length > 0) {
            sql.append(" ON DUPLICATE KEY UPDATE ");
            sql.append(SqlBuilder.buildFieldsWithQuestion(fields));
        }

//        System.out.println("batchInsUp的SQL为：" + sql);

        getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                Map<String, Object> mapObj = list.get(i);
                Integer pos = 1;

                //添加insert字段，数据通过Map去List里面拿
                for (int j = 0, len = fields.length; j < len; j++) {
                    log.debug("添加第【" + pos + "】条记录，数据为：【" + mapObj.get(fields[j]) + "】");
                    ps.setObject(pos, mapObj.get(fields[j]));
                    pos++;
                }

                // 添加update的字段列表，数据通过Map去List里面拿
                if (updateFields != null && updateFields.length > 0) {
                    for (int j = 0, len = updateFields.length; j < len; j++) {
                        log.debug("添加第【" + pos + "】条记录，数据为：【" + mapObj.get(updateFields[j]) + "】");
                        ps.setObject(pos, mapObj.get(updateFields[j]));
                        pos++;
                    }
                }
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    /**
     * 批量插入数据，由于JDBCTemplate不支持批量插入后返回批量id，所以此处使用jdbc原生的方法实现此功能
     *
     * @param tableName
     * @param fields
     * @param list
     * @return
     * @throws Exception
     */
    public List<Long> batchAddWithID(String tableName, String[] fields, final List<Map<String, Object>> list) throws Exception {

        StringBuffer sql = new StringBuffer("insert into ")
                .append(tableName)
                .append("(")
                .append(SqlBuilder.buildFields(fields))
                .append(") values (")
                .append(SqlBuilder.buildQuestion(fields))
                .append(");");

        Connection con = getJdbcTemplate().getDataSource().getConnection();
        con.setAutoCommit(false);
        PreparedStatement pstmt = con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);

        for (Map<String, Object> map : list) {
            for (int j = 0, len = fields.length; j < len; j++) {
                pstmt.setObject(j + 1, map.get(fields[j]));
            }
            pstmt.addBatch();
        }

        pstmt.executeBatch();
        con.commit();
        ResultSet rs = pstmt.getGeneratedKeys(); //获取结果
        List<Long> ids = new ArrayList<Long>();
        while (rs.next()) {
            ids.add(rs.getLong(1));//取得ID
        }
        con.close();
        pstmt.close();
        rs.close();
        return ids;
    }

    /**
     * 更新
     *
     * @param sql    自定义更新sql
     * @param params 查询条件对应的参数(List<Object>)
     * @return int 更新的数量
     */
    public int update(String sql, List<Object> params) {
        //String sql="update person set name=? where id=?";
        return getJdbcTemplate().update(sql, params.toArray());
    }

    /**
     * 修改数据
     *
     * @param map
     * @param whereMap
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void update(Map<String, Object> map, LinkedHashMap<String, Object> whereMap) {
        Pair<String, Object[]> pair = SqlBuilder.buildUpdateSql(tableName, map, whereMap);
        getJdbcTemplate().update(pair.getKey(), pair.getValue());
    }

    /**
     * @param listData
     */
    public void batchUpdate(List<ModelPair> listData) {
        String sql = "update tb_tdk_results set first_category= ? where source_keywords=?";
//        jdbcTemplate.update(sql, new Object[]{category, keyword});

        //        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                DoubanMovie item = listData.get(i);
//                ps.setString(1, item.getName());
//                ps.setString(2, item.getIntroduce());
//                ps.setDouble(3, item.getScore());
//                ps.setString(4, item.getDirector());
//                ps.setString(5, item.getScriptwriter());
//                ps.setString(6, item.getActor());
//                ps.setString(7, item.getUrl());
//                ps.setString(8, item.getCover());
//                ps.setInt(9, 0);
//                ps.setString(10, DateUtil.getNowTime());
//                ps.setString(11, DateUtil.getNowTime());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return listData.size();
//            }
//        });

        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ModelPair baiduResult = listData.get(i);
                ps.setString(1, baiduResult.getValue().toString());
                ps.setString(2, baiduResult.getKey());
            }

            @Override
            public int getBatchSize() {
                return listData.size();
            }
        });

    }

    /**
     * 修改数据
     *
     * @param tableName 表名
     * @param map       Map存放要修改的数据
     * @param whereMap  Where条件，将会以And连接
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void update(String tableName, Map<String, Object> map, LinkedHashMap<String, Object> whereMap) {

        List<Object> params = new ArrayList<Object>();

        StringBuffer sql = new StringBuffer("update ")
                .append(tableName).append(" set ");

        StringBuffer temp = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            temp.append(NamingUtil.camelToUnderline(entry.getKey())).append("=").append("?,");
            params.add(entry.getValue());
        }
        sql.append(temp.deleteCharAt(temp.length() - 1));
        if (whereMap != null) {
            sql.append(" where 1=1 ");
            for (Map.Entry<String, Object> entry : whereMap.entrySet()) {
                sql.append(" and ").append(NamingUtil.camelToUnderline(entry.getKey())).append("?");
                params.add(entry.getValue());
            }
        }
        //sql.append(";");

        getJdbcTemplate().update(sql.toString(), params.toArray());
    }

    /**
     * 批量更新
     *
     * @param fields
     * @param whereFields
     * @param list
     */
    public void updateBatch(String[] fields, LinkedHashMap<String, String> whereFields, final List<Map<String, Object>> list) {

        StringBuffer sql = new StringBuffer("update ")
                .append(tableName).append(" set ");

        final List<String> paramKeys = new ArrayList<String>();
        StringBuffer temp = new StringBuffer();
        for (String ft : fields) {
            temp.append(NamingUtil.camelToUnderline(ft)).append("=").append("?,");
            paramKeys.add(ft);
        }
        sql.append(temp.deleteCharAt(temp.length() - 1));

        if (whereFields != null) {
            sql.append(" where 1=1 ");
            for (Map.Entry<String, String> entry : whereFields.entrySet()) {
                sql.append(" and ").append(NamingUtil.camelToUnderline(entry.getKey())).append(" ").append(entry.getValue()).append(" ").append("?");
                paramKeys.add(entry.getKey());
            }
        }
        sql.append(";");

        getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                for (int j = 0, len = paramKeys.size(); j < len; j++) {
                    ps.setObject(j + 1, list.get(i).get(paramKeys.get(j)));
                }
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public Integer delete(Long id) {
        StringBuffer sql = new StringBuffer("delete from ")
                .append(tableName).append(" where id=?;");
        return getJdbcTemplate().update(sql.toString(), new Object[]{id});
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    public void deleteBatch(final Long[] ids) {
        StringBuffer sql = new StringBuffer("delete from ")
                .append(tableName).append(" where id=?;");

        getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, ids[i]);
            }

            @Override
            public int getBatchSize() {
                return ids.length;
            }
        });
    }


    public Integer count() {
        return count("");
    }

    /**
     * 根据条件查询记录总数
     *
     * @param where
     * @return
     */
    public Integer count(String where) {
        if (StringUtils.isNotEmpty(where)) {
            return getJdbcTemplate().queryForObject("select count(1) from " + tableName + " where " + where, Integer.class);
        }
        return getJdbcTemplate().queryForObject("select count(1) from " + tableName, Integer.class);
    }

    /**
     * 根据Where条件求Count
     *
     * @param where
     * @return
     */
    public int count(Map<String, String> where) {
        StringBuffer sql = new StringBuffer("SELECT COUNT(1) FROM " + tableName)
                .append(SqlBuilder.buildWhere(where));

        return getJdbcTemplate().queryForObject(sql.toString(), Integer.class);
    }

    /**
     * 总数
     *
     * @param tableName 表名
     * @param alias     主表别名
     * @param where     查询条件
     * @return
     * @throws Exception
     */
    public long count(String tableName, String alias, Map<String, String> where) throws Exception {
        String sql = SqlBuilder.buildCountSql(tableName, alias, where);
        return getJdbcTemplate().queryForObject(sql, Long.class);
    }

    /**
     * 自定义查询
     *
     * @param sql
     * @return
     */
    public Map<String, Object> findOne(String sql) {
        return getJdbcTemplate().queryForMap(sql);
    }

    /**
     * 自定义传参查询
     *
     * @param sql
     * @param params
     * @return
     */
    public Map<String, Object> findOne(String sql, Object[] params) {
        return getJdbcTemplate().queryForMap(sql, params);
    }

    /**
     * 根据id字段进行查询
     *
     * @param id
     * @return
     */
    public Map<String, Object> findById(String id) {
        return getJdbcTemplate().queryForMap("select * from " + this.tableName + " where id=" + id);
    }

    /**
     * 总页数
     *
     * @param pageSize
     * @return
     */
    public Integer getTotalPage(Integer pageSize) {
        return PageUtil.totalPage(count(), pageSize);
    }

    /**
     * 取总页数
     *
     * @param pageSize
     * @param where
     * @return
     */
    public Integer getTotalPage(Integer pageSize, String where) {
        return PageUtil.totalPage(count(where), pageSize);
    }

    /**
     * 新的根据分页查看数据
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> findByPage(Integer pageNo, Integer pageSize) {
        pageNo = pageNo > 0 ? pageNo : 1;
        Integer offset = (pageNo - 1) * pageSize;
        String sql = StrUtil.format("select * from {} br WHERE br.id>=(SELECT id from {} ORDER BY id asc limit {},1 ) limit {}", this.tableName, this.tableName, offset, pageSize);

        return getJdbcTemplate().queryForList(sql);
    }

    /**
     * 原生的 queryForList 不能直接转到实体，会产生异常，原生只支持基本类型转换，不支持实体类型转换
     *
     * @param sql
     * @param elementType
     * @param <T>
     * @return
     */
    public <T> List<T> queryForList(String sql, Class<T> elementType) {
        List<T> listResult = Lists.newArrayList();

        log.debug("queryForList查询语句为：{}", sql);
        List<Map<String, Object>> listQueryResultMap = getJdbcTemplate().queryForList(sql);
        if (CollectionUtils.isEmpty(listQueryResultMap)) {
            return null;
        }
        listQueryResultMap.forEach(item -> {
            listResult.add(BeanUtil.mapToBean(item, elementType, true));
        });
        return listResult;
    }

}
