package com.lingdonge.db.repository;

import com.lindonge.core.page.PageBean;
import com.lindonge.core.reflect.NamingUtil;
import com.lingdonge.db.util.JdbcUtil;
import com.lingdonge.db.util.SqlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取超类上的泛型
 * 使用时：public class UserService extends BaseHibernateDao<User, Integer>{}
 * UserService userService = new UserService();
 * System.out.println(userService.getClass());
 *
 * @param <T>
 * @param <ID>
 */
public abstract class BaseRepositoryDao<T, ID extends Serializable> {

    public abstract JdbcTemplate getJdbcTemplate();

    protected abstract RowMapper<T> getRowMapper();

//    private RowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(entityClass);
//    private RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);

    private Class<T> entityClass;

    public Class<T> getClazz() {
        return entityClass;
    }

    /**
     * 设置一些操作的常量
     */
    public static final String SQL_INSERT = "insert";
    public static final String SQL_UPDATE = "update";
    public static final String SQL_DELETE = "delete";

    private String tableName = "";
    private String pkColumn = "";

    @SuppressWarnings("unchecked")
    public BaseRepositoryDao() {
//        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
//        entityClass = (Class<T>) type.getActualTypeArguments()[0];
//        System.out.println("Dao实现类是：" + entityClass.getName());

        //当前对象的直接超类的 Type
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            //参数化类型
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            //返回表示此类型实际类型参数的 Type 对象的数组
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.entityClass = (Class<T>) actualTypeArguments[0];
        } else {
            this.entityClass = (Class<T>) genericSuperclass;
        }

        try {
            tableName = JdbcUtil.getTableName(entityClass);
            pkColumn = JdbcUtil.getPkColumn(entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(entityClass){
//        @Override
//        protected void initBeanWrapper(BeanWrapper bw) {
//            bw.registerCustomEditor(Gender.class, new GenderEditor());
//            super.initBeanWrapper(bw);
//        }
//    };

    public int save(T entity) {
        String sql = this.makeSql(SQL_INSERT);
        Object[] args = this.setArgs(entity, SQL_INSERT);
        int[] argTypes = this.setArgTypes(entity, SQL_INSERT);
        return getJdbcTemplate().update(sql.toString(), args, argTypes);
    }

    /**
     * @param entity
     * @return
     */
    public int update(T entity) {
        String sql = this.makeSql(SQL_UPDATE);
        Object[] args = this.setArgs(entity, SQL_UPDATE);
        int[] argTypes = this.setArgTypes(entity, SQL_UPDATE);
        return getJdbcTemplate().update(sql, args, argTypes);
    }

    /**
     * 查询全部，适用于少量数据查询全部
     *
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<T> findAll() {
        String sql = "SELECT * FROM `" + this.tableName + "`";
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        return getJdbcTemplate().query(sql, rowMapper);
    }

    /**
     * ID查询
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public T findById(Serializable id) throws Exception {
        String sql = "SELECT * FROM `" + tableName + "` WHERE " + this.pkColumn + "=?";
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        return getJdbcTemplate().query(sql, rowMapper, id).get(0);
    }

    /**
     * ID查询指定字段(不需要传表名)
     *
     * @param fields
     * @param id
     * @param rowMapper
     * @param <T>
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public <T> T findById(String[] fields, Serializable id, RowMapper<T> rowMapper) throws Exception {
        StringBuffer sql = new StringBuffer("select ");
        sql.append(SqlBuilder.buildFields(fields))
                .append(" from `")
                .append(tableName)
                .append("` where " + pkColumn + "=? limit 1;");
        return getJdbcTemplate().queryForObject(sql.toString(), new Object[]{id}, rowMapper);
    }

    /**
     * ID查询单个字段
     *
     * @param field
     * @param id
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public <T> T findById(String field, Serializable id, Class<T> clazz) throws Exception {
        StringBuffer sql = new StringBuffer("select ");
        sql.append(field).append(" from `").append(tableName).append("` where " + this.pkColumn + "=? limit 1;");
        return getJdbcTemplate().queryForObject(sql.toString(), new Object[]{id}, clazz);
    }

    /**
     * 查询单个对象
     *
     * @param fields    返回的字段
     * @param where     where条件
     * @param params    where参数值
     * @param groupby   group条件
     * @param orderby   order条件
     * @param rowMapper Row映射关系
     * @param <T>
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public <T> T findByObj(String[] fields, String where, List<Object> params, String
            groupby, LinkedHashMap<String, String> orderby, RowMapper<T> rowMapper) throws Exception {
        StringBuffer sql = new StringBuffer("select ");
        sql.append(SqlBuilder.buildFields(fields))
                .append(" from ")
                .append(tableName)
                .append(StringUtils.isEmpty(where) ? "" : " where " + where).append(" ")
                .append(StringUtils.isEmpty(groupby) ? "" : groupby).append(SqlBuilder.buildOrderby(orderby));

        if (params == null) {
            params = new ArrayList<Object>();
        }
        sql.append(" limit 1;");
        return getJdbcTemplate().queryForObject(sql.toString(), params.toArray(), rowMapper);
    }

    /**
     * 简化版单个对象查询
     *
     * @param fields
     * @param where
     * @param params
     * @param rowMapper
     * @param <T>
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public <T> T findByObj(String[] fields, String where, List<Object> params, RowMapper<T> rowMapper) throws Exception {
        return this.findByObj(fields, where, params, null, null, rowMapper);
    }


    /**
     * 简化版本，快速查询分页
     *
     * @param pageNumber 页面数量
     * @param pageSize   每页数据量
     * @param alias      主表别名
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public PageBean findByPage(int pageNumber, int pageSize, String alias) throws Exception {
        return findByPage(pageNumber, pageSize, null, alias, null, null, null, getRowMapper());
    }

    /**
     * 分页查询
     *
     * @param pageNumber 第几页
     * @param pageSize   每页数据量
     * @param fields     返回的字段
     * @param alias      主表别名
     * @param where      where条件
     * @param groupby    group条件
     * @param orderby    order条件
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public PageBean findByPage(int pageNumber, int pageSize, String[] fields, String alias, Map<String, String> where, String groupby, LinkedHashMap<String, String> orderby) throws Exception {
        return findByPage(pageNumber, pageSize, fields, alias, where, groupby, orderby, getRowMapper());
    }

    /**
     * 分页查询
     *
     * @param pageNumber 第几页
     * @param pageSize   每页数据量
     * @param fields     返回的字段
     * @param alias      主表别名
     * @param where      where条件
     * @param groupby    group条件
     * @param orderby    order条件
     * @param rowMapper  Row映射关系
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public PageBean findByPage(int pageNumber, int pageSize, String[] fields, String alias, Map<String, String> where, String groupby, LinkedHashMap<String, String> orderby, RowMapper<T> rowMapper) throws
            Exception {
        if (pageNumber < 1 || pageSize < 1) {
            //当前页面页数和页面记录展示条数不能小于1
            return new PageBean(pageNumber, pageSize);
        }

        long totalRow = 0;//总数
        int totalPage = 0;//总页码
//        totalRow = this.count(tableName, alias, where);

        if (totalRow % pageSize == 0) {
            totalPage = (int) (totalRow / pageSize);
        } else {
            totalPage = (int) (totalRow / pageSize) + 1;
        }

        //获取主表名字
        StringBuilder sql = new StringBuilder("select ");

        // 如果fields为Null，查所有字段
        if (fields != null && fields.length > 0) {
            sql.append(SqlBuilder.buildFields(fields));
        } else {
            sql.append("*");
        }

        sql.append(" from " + tableName);

        sql.append(" INNER JOIN ( select " + alias.trim() + "." + this.pkColumn + " from ").append("`" + this.tableName + "`").append(" " + alias.trim() + " ");
        if (where != null && where.size() > 0) {
            sql.append(" WHERE "); // 注意不是where
            boolean flag = false;
            for (Map.Entry<String, String> me : where.entrySet()) {

                String columnName = me.getKey();
                String columnValue = me.getValue();
                if (columnName.trim().startsWith(alias.trim())) {
                    flag = true;
                    sql.append(NamingUtil.camelToUnderline(columnName)).append(" ").append(columnValue).append(" AND "); // 没有考虑or的情况
                }

            }
            if (!flag) {
                int index = sql.lastIndexOf("WHERE");
                if (index > 0)
                    sql = new StringBuilder(sql.substring(0, index));
            }
            int endIndex = sql.lastIndexOf("AND");
            if (endIndex > 0) {
                sql = new StringBuilder(sql.substring(0, endIndex));
            }
        } else {
            sql.append(" ");
        }


        // sql.append(" limit ").append("?").append(",").append("?");
        sql.append(" ) ");
        sql.append(" as page USING (" + this.pkColumn + ") ");


//        sql.append(" join ( select " + this.getColumnId() + " from `").append(annotation.name()).append("`");
//        sql.append(" limit ").append(1).append(",").append(totalRow==0?1:totalRow);
//        sql.append(" ) wi ");
//        sql.append(" on a." + this.getColumnId()).append("=").append("wi." + this.getColumnId());
//

        if (where != null && where.size() > 0) {
            sql.append(" WHERE "); // 注意不是where
            boolean leftflag = false;
            for (Map.Entry<String, String> me : where.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                if (!columnName.trim().startsWith(alias.trim())) {
                    leftflag = true;
                    sql.append(NamingUtil.camelToUnderline(columnName)).append(" ").append(columnValue).append(" AND "); // 没有考虑or的情况
                }

            }
            if (!leftflag) {
                int index = sql.lastIndexOf("WHERE");
                if (index > 0)
                    sql = new StringBuilder(sql.substring(0, index));
            }
            if (leftflag) {
                int endIndex = sql.lastIndexOf("AND");
                if (endIndex > 0) {
                    sql = new StringBuilder(sql.substring(0, endIndex));
                }
            }

        } else {
            sql.append(" ");
        }

        if (StringUtils.isNotEmpty(groupby)) {
            sql.append(groupby).append(" ");
        }

        if (null != orderby && orderby.size() > 0) {
            sql.append(SqlBuilder.buildOrderby(orderby)).append(" ");
        }

        sql.append(" limit ?,?");
        // 判断当前页面是否大于最大页面
        pageNumber = pageNumber >= totalPage ? totalPage : pageNumber;
        Object[] args = {(pageNumber - 1) * pageSize < 0 ? 0 : (pageNumber - 1) * pageSize, pageSize};

        //System.out.print(sql.toString());
        List<T> result = getJdbcTemplate().query(sql.toString(), args, rowMapper);
        return new PageBean(result, pageNumber, pageSize, ((Number) totalRow).intValue());
    }


    public int delete(T entity) {
        String sql = this.makeSql(SQL_DELETE);
        Object[] args = this.setArgs(entity, SQL_DELETE);
        int[] argTypes = this.setArgTypes(entity, SQL_DELETE);
        return getJdbcTemplate().update(sql, args, argTypes);
    }

    public int delete(Serializable id) {
        String sql = "DELETE FROM " + tableName + " WHERE id=?";
        return getJdbcTemplate().update(sql, id);
    }

    public void deleteAll() {
        String sql = " TRUNCATE TABLE " + tableName;
        getJdbcTemplate().execute(sql);
    }


    public int count(String whereSql, Object[] objects) {
        StringBuffer sql = new StringBuffer("select count(*) from ");
        sql.append(this.tableName);
        sql.append(" o ").append(whereSql);
        return getJdbcTemplate().queryForObject(sql.toString(), objects, Integer.class);
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
     * 集合查询-简化版
     *
     * @param fields    返回的字段
     * @param where     where条件
     * @param params    where参数值
     * @param rowMapper Row映射关系
     * @param <T>
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public <T> List<T> findByList(String[] fields, String where,
                                  List<Object> params, RowMapper<T> rowMapper) throws Exception {
        return findByList(fields, 0, where, params, null, null, rowMapper);
    }

    /**
     * 集合查询-返回所有结果
     *
     * @param fields    返回的字段
     * @param where     where条件
     * @param params    where参数值
     * @param groupby   group条件
     * @param orderby   order条件
     * @param rowMapper Row映射关系
     * @param <T>
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public <T> List<T> findByList(String[] fields, String where,
                                  List<Object> params, String groupby, LinkedHashMap<String, String> orderby, RowMapper<T> rowMapper) throws Exception {
        return findByList(fields, 0, where, params, groupby, orderby, rowMapper);
    }

    /**
     * 集合查询-限制返回结果的条数
     *
     * @param fields    返回的字段
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
    public <T> List<T> findByList(String[] fields, int maxresult, String where,
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
                .append(StringUtils.isEmpty(groupby) ? "" : groupby).append(SqlBuilder.buildOrderby(orderby));
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
     * 分页查询
     *
     * @param pageNumber 第几页
     * @param pageSize   每页数据量
     * @param fields     返回的字段
     * @param tableName  表名
     * @param alias      主表别名
     * @param where      where条件
     * @param groupby    group条件
     * @param orderby    order条件
     * @param rowMapper  Row映射关系
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public PageBean findByPage(int pageNumber, int pageSize, String[] fields, String tableName, String alias, Map<String, String> where, String groupby, LinkedHashMap<String, String> orderby, RowMapper<T> rowMapper) throws
            Exception {
        if (pageNumber < 1 || pageSize < 1) {
            //当前页面页数和页面记录展示条数不能小于1
            return new PageBean(pageNumber, pageSize);
        }

        long totalRow = 0;//总数
        int totalPage = 0;//总页码
        totalRow = this.count(tableName, alias, where);

        if (totalRow % pageSize == 0) {
            totalPage = (int) (totalRow / pageSize);
        } else {
            totalPage = (int) (totalRow / pageSize) + 1;
        }

        //获取主表名字
        StringBuilder sql = new StringBuilder("select ");

        // 如果fields为Null，查所有字段
        if (fields != null && fields.length > 0) {
            sql.append(SqlBuilder.buildFields(fields));
        } else {
            sql.append("*");
        }

        sql.append(" from " + tableName);


        sql.append(" INNER JOIN ( select " + alias.trim() + "." + pkColumn + " from ").append("`" + tableName + "`").append(" " + alias.trim() + " ");
        if (where != null && where.size() > 0) {
            sql.append(" WHERE "); // 注意不是where
            boolean flag = false;
            for (Map.Entry<String, String> me : where.entrySet()) {

                String columnName = me.getKey();
                String columnValue = me.getValue();
                if (columnName.trim().startsWith(alias.trim())) {
                    flag = true;
                    sql.append(NamingUtil.camelToUnderline(columnName)).append(" ").append(columnValue).append(" AND "); // 没有考虑or的情况
                }

            }
            if (!flag) {
                int index = sql.lastIndexOf("WHERE");
                if (index > 0)
                    sql = new StringBuilder(sql.substring(0, index));
            }
            int endIndex = sql.lastIndexOf("AND");
            if (endIndex > 0) {
                sql = new StringBuilder(sql.substring(0, endIndex));
            }
        } else {
            sql.append(" ");
        }


        // sql.append(" limit ").append("?").append(",").append("?");
        sql.append(" ) ");
        sql.append(" as page USING (" + pkColumn + ") ");


//        sql.append(" join ( select " + this.getColumnId() + " from `").append(annotation.name()).append("`");
//        sql.append(" limit ").append(1).append(",").append(totalRow==0?1:totalRow);
//        sql.append(" ) wi ");
//        sql.append(" on a." + this.getColumnId()).append("=").append("wi." + this.getColumnId());
//

        if (where != null && where.size() > 0) {
            sql.append(" WHERE "); // 注意不是where
            boolean leftflag = false;
            for (Map.Entry<String, String> me : where.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                if (!columnName.trim().startsWith(alias.trim())) {
                    leftflag = true;
                    sql.append(NamingUtil.camelToUnderline(columnName)).append(" ").append(columnValue).append(" AND "); // 没有考虑or的情况
                }

            }
            if (!leftflag) {
                int index = sql.lastIndexOf("WHERE");
                if (index > 0)
                    sql = new StringBuilder(sql.substring(0, index));
            }
            if (leftflag) {
                int endIndex = sql.lastIndexOf("AND");
                if (endIndex > 0) {
                    sql = new StringBuilder(sql.substring(0, endIndex));
                }
            }

        } else {
            sql.append(" ");
        }

        if (StringUtils.isNotEmpty(groupby)) {
            sql.append(groupby).append(" ");
        }

        if (null != orderby && orderby.size() > 0) {
            sql.append(SqlBuilder.buildOrderby(orderby)).append(" ");
        }


        sql.append(" limit ?,?");
        // 判断当前页面是否大于最大页面
        pageNumber = pageNumber >= totalPage ? totalPage : pageNumber;
        Object[] args = {(pageNumber - 1) * pageSize < 0 ? 0 : (pageNumber - 1) * pageSize, pageSize};

        //System.out.print(sql.toString());
        List<T> result = getJdbcTemplate().query(sql.toString(), args, rowMapper);
        return new PageBean(result, pageNumber, pageSize, ((Number) totalRow).intValue());
    }


//    /**
//     * 查询
//     *
//     * @param page     分页参数
//     * @param whereSql 查询条件（例:o.name=?）
//     * @param params   查询条件对应的参数(List<Object>)
//     * @return List<T>
//     */
//    public List<T> query(Pager page, String whereSql, List<Object> params) {
//        List<Object> paramList = new ArrayList<Object>();
//        if (!com.kyle.utility.token.StringUtils.isEmpty(whereSql) && !JudgeUtil.isEmpty(params)) {
//            for (Object object : params) {
//                if (object instanceof Enum) {
//                    paramList.add(((Enum) object).ordinal());
//                } else {
//                    paramList.add(object);
//                }
//            }
//        }
//        String sql = getSql(page, whereSql, null);
//
//        if (page != null) {
//            paramList.add(page.getStartIndex());
//            paramList.add(page.getPageSize());
//        }
//        return (List<T>) getJdbcTemplate().query(sql, rowMapper, paramList.toArray());
//    }

//    /**
//     * 查询
//     *
//     * @param page    分页参数
//     * @param orderby 排序条件（LinkedHashMap<String, String>）
//     * @return List<T>
//     * @author lqy
//     * @since 2015-10-18
//     */
//    public List<T> query(Pager page, LinkedHashMap<String, String> orderby) {
//        List<Object> paramsList = new ArrayList<Object>();
//
//        String sql = getSql(page, null, orderby);
//
//        if (page != null) {
//            paramsList.add(page.getStartIndex());
//            paramsList.add(page.getPageSize());
//        }
//        return (List<T>) getJdbcTemplate().query(sql, rowMapper, paramsList.toArray());
//    }

//    /**
//     * 查询
//     *
//     * @param page     分页参数
//     * @param whereSql 查询条件（例:o.name=?）
//     * @param params   查询条件对应的参数(List<Object>)
//     * @param orderby  排序条件（LinkedHashMap<String, String>）
//     * @return List<T>
//     * @author lqy
//     * @since 2015-10-18
//     */
//    @Override
//    public List<T> query(Pager page, String whereSql, List<Object> params, LinkedHashMap<String, String> orderby) {
//        List<Object> paramsList = new ArrayList<Object>();
//        if (!com.kyle.utility.token.StringUtils.isEmpty(whereSql) && !JudgeUtil.isEmpty(params)) {
//            for (Object object : params) {
//                if (object instanceof Enum) {
//                    paramsList.add(((Enum) object).ordinal());
//                } else {
//                    paramsList.add(object);
//                }
//            }
//        }
//
//        String sql = getSql(page, whereSql, orderby);
//        //System.out.println("sql ="+sql);
//
//        if (page != null) {
//            paramsList.add(page.getStartIndex());
//            paramsList.add(page.getPageSize());
//        }
//
//        return (List<T>) getJdbcTemplate().query(sql, rowMapper, paramsList.toArray());
//    }


    public List<T> find(int pageNo, int pageSize, Map<String, String> where, LinkedHashMap<String, String> orderby) {
        // where 与 order by 要写在select * from table 的后面，而不是where rownum<=? )
        // where rn>=?的后面
        StringBuffer sql = new StringBuffer(
                " SELECT * FROM (SELECT t.*,ROWNUM rn FROM (SELECT * FROM " + entityClass.getSimpleName());
        if (where != null && where.size() > 0) {
            sql.append(" WHERE "); // 注意不是where
            for (Map.Entry<String, String> me : where.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                sql.append(columnName).append(" ").append(columnValue).append(" AND "); // 没有考虑or的情况
            }
            int endIndex = sql.lastIndexOf("AND");
            if (endIndex > 0) {
                sql = new StringBuffer(sql.substring(0, endIndex));
            }
        }
        if (orderby != null && orderby.size() > 0) {
            sql.append(" ORDER BY ");
            for (Map.Entry<String, String> me : orderby.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                sql.append(columnName).append(" ").append(columnValue).append(",");
            }
            sql = sql.deleteCharAt(sql.length() - 1);
        }
        sql.append(" ) t WHERE ROWNUM<=? ) WHERE rn>=? ");
        System.out.println("SQL=" + sql);
        Object[] args = {pageNo * pageSize, (pageNo - 1) * pageSize + 1};
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        return getJdbcTemplate().query(sql.toString(), args, rowMapper);
    }

    // 设置参数类型（写的不全，只是一些常用的）
    private int[] setArgTypes(T entity, String sqlFlag) {
        Field[] fields = entityClass.getDeclaredFields();
        if (sqlFlag.equals(SQL_INSERT)) {
            int[] argTypes = new int[fields.length];
            try {
                for (int i = 0; argTypes != null && i < argTypes.length; i++) {
                    fields[i].setAccessible(true); // 暴力反射
                    if (fields[i].get(entity).getClass().getName().equals("java.lang.String")) {
                        argTypes[i] = Types.VARCHAR;
                    } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Double")) {
                        argTypes[i] = Types.DECIMAL;
                    } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Integer")) {
                        argTypes[i] = Types.INTEGER;
                    } else if (fields[i].get(entity).getClass().getName().equals("java.token.Date")) {
                        argTypes[i] = Types.DATE;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            int[] tempArgTypes = new int[fields.length];
            int[] argTypes = new int[fields.length];
            try {
                for (int i = 0; tempArgTypes != null && i < tempArgTypes.length; i++) {
                    fields[i].setAccessible(true); // 暴力反射
                    if (fields[i].get(entity).getClass().getName().equals("java.lang.String")) {
                        tempArgTypes[i] = Types.VARCHAR;
                    } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Double")) {
                        tempArgTypes[i] = Types.DECIMAL;
                    } else if (fields[i].get(entity).getClass().getName().equals("java.lang.Integer")) {
                        tempArgTypes[i] = Types.INTEGER;
                    } else if (fields[i].get(entity).getClass().getName().equals("java.token.Date")) {
                        tempArgTypes[i] = Types.DATE;
                    }
                }
                System.arraycopy(tempArgTypes, 1, argTypes, 0, tempArgTypes.length - 1); // 数组拷贝
                argTypes[argTypes.length - 1] = tempArgTypes[0];

            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;

        } else if (sqlFlag.equals(SQL_DELETE)) {
            int[] argTypes = new int[1]; // 长度是1
            try {
                fields[0].setAccessible(true); // 暴力反射
                if (fields[0].get(entity).getClass().getName().equals("java.lang.String")) {
                    argTypes[0] = Types.VARCHAR;
                } else if (fields[0].get(entity).getClass().getName().equals("java.lang.Integer")) {
                    argTypes[0] = Types.INTEGER;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return argTypes;
        }
        return null;
    }

    // 设置参数
    private Object[] setArgs(T entity, String sqlFlag) {
        Field[] fields = entityClass.getDeclaredFields();
        if (sqlFlag.equals(SQL_INSERT)) {
            Object[] args = new Object[fields.length];
            for (int i = 0; args != null && i < args.length; i++) {
                try {
                    fields[i].setAccessible(true); // 暴力反射
                    args[i] = fields[i].get(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return args;
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            Object[] tempArr = new Object[fields.length];
            for (int i = 0; tempArr != null && i < tempArr.length; i++) {
                try {
                    fields[i].setAccessible(true); // 暴力反射
                    tempArr[i] = fields[i].get(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Object[] args = new Object[fields.length];
            System.arraycopy(tempArr, 1, args, 0, tempArr.length - 1); // 数组拷贝
            args[args.length - 1] = tempArr[0];
            return args;
        } else if (sqlFlag.equals(SQL_DELETE)) {
            Object[] args = new Object[1]; // 长度是1
            fields[0].setAccessible(true); // 暴力反射
            try {
                args[0] = fields[0].get(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return args;
        }
        return null;

    }

    /**
     * 组装SQL
     *
     * @param sqlFlag
     * @return
     */
    private String makeSql(String sqlFlag) {
        StringBuffer sql = new StringBuffer();
        Field[] fields = entityClass.getDeclaredFields();
        if (sqlFlag.equals(SQL_INSERT)) {
            sql.append(" INSERT INTO " + entityClass.getSimpleName());
            sql.append("(");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();
                sql.append(column).append(",");
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES (");
            for (int i = 0; fields != null && i < fields.length; i++) {
                sql.append("?,");
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
        } else if (sqlFlag.equals(SQL_UPDATE)) {
            sql.append(" UPDATE " + entityClass.getSimpleName() + " SET ");
            for (int i = 0; fields != null && i < fields.length; i++) {
                fields[i].setAccessible(true); // 暴力反射
                String column = fields[i].getName();
                if (column.equals("id")) { // id 代表主键
                    continue;
                }
                sql.append(column).append("=").append("?,");
            }
            sql = sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE id=?");
        } else if (sqlFlag.equals(SQL_DELETE)) {
            sql.append(" DELETE FROM " + entityClass.getSimpleName() + " WHERE id=?");
        }
        System.out.println("SQL=" + sql);
        return sql.toString();

    }


    public static void main(String[] args) {
        // 测试
//        System.out.println(BaseRepositoryDao.buildQuestion(new String[]{"2", "1", "3", "5"}));
//        if("a.w_s".startsWith("b"))
//            System.out.print(1);
    }

}
