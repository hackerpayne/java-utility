package com.lingdonge.db.util;

import com.lingdonge.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * 基于JdbcTemplate的分页
 */
@Slf4j
public class Pagination<T> {

//    public List<Map<String, Object>> getPageListAllCol(String tableName,String where,int currentPage,int numPerPage){
//        String sql = "select * from " + tableName + where;
//        Pagination page = new Pagination(sql, currentPage, numPerPage, jdbcTemplate);
//        return page.getResultList();
//    }

    //一页显示的记录数，默认100条
    private int numPerPage = 100;

    //记录总数
    private int totalRows;

    //总页数
    private int totalPages;

    //当前页码
    private int currentPage = 1;

    //起始行数
    private int startIndex;

    //结束行数
    private int lastIndex;

    //结果集存放List
//    private List<Map<String, Object>> resultList;

    private JdbcTemplate jdbcTemplate;

    private String sql;

    /**
     * 构造函数
     */
    public Pagination() {

    }

    /**
     * @param sql
     */
    public Pagination(String sql) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("Pagination.jdbcTemplate is null,please initial it first. ");
        } else if (sql.equals("")) {
            throw new IllegalArgumentException("Pagination.sql is empty,please initial it first. ");
        }

        new Pagination(sql, currentPage, numPerPage, jdbcTemplate);
    }

    /**
     * 分页构造函数
     *
     * @param sql          包含筛选条件的sql，但不包含分页相关约束，如mysql的limit
     * @param currentPage  当前页
     * @param numPerPage   每页记录数
     * @param jdbcTemplate JdbcTemplate实例
     */
    public Pagination(String sql, int currentPage, int numPerPage, JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("Pagination.jdbcTemplate is null");
        } else if (sql == null || sql.equals("")) {
            throw new IllegalArgumentException("Pagination.sql is empty");
        }

        this.sql = sql;
        this.jdbcTemplate = jdbcTemplate;

        //设置每页显示记录数
        setNumPerPage(numPerPage);

        //设置要显示的页数
        setCurrentPage(currentPage);

        //计算总记录数
        StringBuilder totalSQL = new StringBuilder(" SELECT count(*) FROM ( ");
        totalSQL.append(sql);
        totalSQL.append(" ) totalTable ");

        setTotalRows(jdbcTemplate.queryForObject(totalSQL.toString(), Integer.class));//总记录数

        setTotalPages();//计算总页数
        setStartIndex();//计算起始行数
        setLastIndex(); //计算结束行数

        log.info(StringUtils.format("SQL【{}】表数据【{}】条，每页【{}】条，共【{}】页", sql, getTotalRows(), getNumPerPage(), getTotalPages()));

//        //使用mysql时直接使用limits
//        StringBuffer paginationSQL = new StringBuffer();
//        paginationSQL.append(sql);
//        paginationSQL.append(" limit " + startIndex + "," + lastIndex);

        //装入结果集
//        setResultList(jdbcTemplate.queryForList(paginationSQL.toString()));
    }

    /**
     * @param totalRows
     * @param table
     * @param mainKey
     * @param sort
     * @param pageSize
     * @param currentPage
     * @return
     */
    public List<T> getPageDataManual(Integer totalRows, String table, String mainKey, String sort, Integer pageSize, Integer currentPage, RowMapper<T> mapper) {

        // 设置基础信息
        setCurrentPage(currentPage);// 设置当前页
        setTotalRows(totalRows);// 设置记录总数
        setNumPerPage(pageSize);// 设置每页数据量大小

        // 可以计算结果了
        setTotalPages();//计算总页数
        setStartIndex();//计算起始行数
        setLastIndex(); //计算结束行数

        String sql = StringUtils.format("select * from {}  br WHERE br.{}>=(SELECT {} from {} ORDER BY  {} {} limit {},1 ) limit {}", table, mainKey, mainKey, table, mainKey, sort, getStartIndex(), pageSize);

//        System.out.println("sql处理结果为：" + sql);

        log.info(StringUtils.format("SQL【{}】表数据【{}】条，每页【{}】条，共【{}】页", sql, getTotalRows(), getNumPerPage(), getTotalPages()));

        List<T> results = jdbcTemplate.query(sql, mapper);
        return results;
    }

    /**
     * 取SQL语句第N页结果
     *
     * @param currentPage
     * @return
     */
    public List<Map<String, Object>> getPageData(Integer currentPage) {
        setCurrentPage(currentPage);// 设置当前页码
        setStartIndex();// 计算起始行数
        setLastIndex(); // 计算结束行数

        // 使用mysql时直接使用limits
        StringBuilder paginationSQL = new StringBuilder();
        paginationSQL.append(sql);
        paginationSQL.append(" limit " + startIndex + "," + numPerPage);

        /** Oracle分页
         StringBuffer paginationSQL = new StringBuffer(" SELECT * FROM ( ");
         paginationSQL.append(" SELECT temp.* ,ROWNUM num FROM ( ");
         paginationSQL.append(sql);
         paginationSQL.append(" ) temp where ROWNUM <= " + lastIndex);
         paginationSQL.append(" ) WHERE num > " + startIndex);
         */

        log.info(StringUtils.format("SQL【{}】表数据【{}】条，每页【{}】条，共【{}】页", paginationSQL.toString(), getTotalRows(), getNumPerPage(), getTotalPages()));

        return jdbcTemplate.queryForList(paginationSQL.toString());

    }

    /**
     * 取SQL语句第N页结果
     *
     * @param currentPage
     * @return
     */
    public List<T> getPageItems(Integer currentPage) {
        setCurrentPage(currentPage);// 设置当前页码
        setStartIndex();// 计算起始行数
        setLastIndex(); // 计算结束行数

        // 使用mysql时直接使用limits
        StringBuilder paginationSQL = new StringBuilder();
        paginationSQL.append(sql);
        paginationSQL.append(" limit " + startIndex + "," + numPerPage);

        /** Oracle分页
         StringBuffer paginationSQL = new StringBuffer(" SELECT * FROM ( ");
         paginationSQL.append(" SELECT temp.* ,ROWNUM num FROM ( ");
         paginationSQL.append(sql);
         paginationSQL.append(" ) temp where ROWNUM <= " + lastIndex);
         paginationSQL.append(" ) WHERE num > " + startIndex);
         */

        log.info(StringUtils.format("SQL【{}】表数据【{}】条，每页【{}】条，共【{}】页", paginationSQL.toString(), getTotalRows(), getNumPerPage(), getTotalPages()));

        return jdbcTemplate.queryForList(paginationSQL.toString(), (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getClass());
//        List<E> result = jdbcTemplate.query(sql.toString(), new Object[] {queryPara}, new BeanPropertyRowMapper<E>(E.class))
    }

    public Class<?> getType() {
        T t = (T) new Object();
        return t.getClass();
    }

    /**
     * 获取当前页码
     *
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 设置当前页码
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * 获取每页数据量
     *
     * @return
     */
    public int getNumPerPage() {
        return numPerPage;
    }

    /**
     * 设置每页数据量
     *
     * @param numPerPage
     */
    public void setNumPerPage(int numPerPage) {
        this.numPerPage = numPerPage;
    }

//    public List<Map<String, Object>> getResultList() {
//        return resultList;
//    }
//
//    public void setResultList(List<Map<String, Object>> resultList) {
//        this.resultList = resultList;
//    }

    public int getTotalPages() {
        return totalPages;
    }

    /**
     * 计算总页数
     */
    private void setTotalPages() {
        if (totalRows % numPerPage == 0) {
            this.totalPages = totalRows / numPerPage;
        } else {
            this.totalPages = (totalRows / numPerPage) + 1;
        }
    }

    /**
     * 取总记录数
     *
     * @return
     */
    public int getTotalRows() {
        return totalRows;
    }

    /**
     * 设置总记录数
     *
     * @param totalRows
     */
    private void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getStartIndex() {
        return startIndex;
    }

    /**
     * 设置页码的起点位置
     */
    public void setStartIndex() {
        this.startIndex = (currentPage - 1) * numPerPage;
    }

    /**
     * 取当前页码的结束位置
     *
     * @return
     */
    public int getLastIndex() {
        return lastIndex;
    }


    /**
     * 设置页码的结束位置
     */
    public void setLastIndex() {
//        log.info("totalRows=" + totalRows);
//        log.info("numPerPage=" + numPerPage);

        if (totalRows < numPerPage) {
            this.lastIndex = totalRows;
        } else if ((totalRows % numPerPage == 0) || (totalRows % numPerPage != 0 && currentPage < totalPages)) {
            this.lastIndex = currentPage * numPerPage;
        } else if (totalRows % numPerPage != 0 && currentPage == totalPages) {//最后一页
            this.lastIndex = totalRows;
        }
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}