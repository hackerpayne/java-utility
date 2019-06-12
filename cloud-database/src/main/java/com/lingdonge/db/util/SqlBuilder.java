package com.lingdonge.db.util;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.lingdonge.core.reflect.NamingUtil;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.db.db.Pager;
import javafx.util.Pair;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqlBuilder {

    /**
     * 组装tableName，因为表名如果有数字，会造成异常
     *
     * @param tableName
     * @return
     */
    public static String buildTableName(String tableName) {
        return "`" + tableName.replaceAll("#", "") + "`";
    }

    /**
     * 生成SQL插入语句
     *
     * @param tableName
     * @param map
     * @return
     */
    public static Pair<String, Object[]> buildInsert(String tableName, Map<String, Object> map) {

        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("insert into ");

        sql.append(buildTableName(tableName)).append("(");

        StringBuffer field = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            field.append(NamingUtil.camelToUnderline(entry.getKey())).append(",");
            params.add(entry.getValue());
        }
        sql.append(field.deleteCharAt(field.length() - 1));
        sql.append(") values (");
        sql.append(buildQuestion(map));
        sql.append(");");
        return new Pair<String, Object[]>(sql.toString(), params.toArray());
    }

    /**
     * @param tableName
     * @param map
     * @param whereMap
     * @return
     */
    public static Pair<String, Object[]> buildUpdateSql(String tableName, Map<String, Object> map, LinkedHashMap<String, Object> whereMap) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("update ");
        sql.append(buildTableName(tableName)).append(" set ");

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
        return new Pair<String, Object[]>(sql.toString(), params.toArray());
    }

    /**
     * 根据条件生成Count语句
     *
     * @param tableName
     * @param countStr
     * @param where
     * @return
     * @throws Exception
     */
    public static String buildCountSql(String tableName, String countStr, Map<String, String> where) throws Exception {

        if (StringUtils.isEmpty(countStr)) {
            throw new RuntimeException("实体缺少主表别名");
        }

        StringBuilder sql = new StringBuilder(" SELECT COUNT(" + countStr + ") FROM " + tableName);
        sql.append(buildWhere(where));
        return sql.toString().trim();
    }

    /**
     * 生成Where语句
     *
     * @param whereMap
     * @return
     */
    public static String buildWhere(Map<String, String> whereMap) {

        StringBuffer sql = new StringBuffer(" WHERE 1=1");
        if (MapUtils.isNotEmpty(whereMap)) {
            sql.append(" WHERE 1=1 ");
            for (Map.Entry<String, String> me : whereMap.entrySet()) {
                String columnName = me.getKey();
                String columnValue = me.getValue();
                sql.append(" AND ").append(NamingUtil.camelToUnderline(columnName)).append(" ").append(columnValue); // 没有考虑or的情况
            }
        }
        return sql.toString();
    }

    /**
     * 生成批处理SQL
     *
     * @param tableName
     * @param listData
     * @return
     */
    public static Pair<String, List<Object[]>> buildBatchInsert(String tableName, final List<Map<String, Object>> listData) {
        List<Object[]> parameters = new ArrayList<Object[]>();

        List<Object> listObjs = Lists.newArrayList();
        List<String> listKeys = Lists.newArrayList();//Insert的Key
        List<String> listCommas = Lists.newArrayList();// 逗号数

        boolean flagSetKeys = false;
        for (Map<String, Object> mapItem : listData) {

            listObjs.clear();

            if (!flagSetKeys) {
                listKeys.clear();
                listCommas.clear();
            }
            for (Map.Entry<String, Object> entry : mapItem.entrySet()) {
                listObjs.add(entry.getValue());

                if (!flagSetKeys) {
                    listKeys.add("`" + entry.getKey() + "`");
                    listCommas.add("?");
                }
            }

            flagSetKeys = true;
            parameters.add(listObjs.toArray());
        }

//        System.out.println(listKeys);
//        System.out.println(listObjs);

        String sql = StrUtil.format("INSERT INTO `{}` ({}) VALUES ({})", tableName, Joiner.on(",").join(listKeys), Joiner.on(",").join(listCommas));
        return new Pair<String, List<Object[]>>(sql, parameters);
    }

    /**
     * 组装字段集，组装成 xx,xx,xx,这种风格
     *
     * @param fields
     * @return
     */
    public static String buildFields(String[] fields) {
        StringBuffer sql = new StringBuffer();

        if (null != fields && fields.length > 0) {
            if (fields != null && fields.length > 0) {
                for (String field : fields) {
                    sql.append(NamingUtil.camelToUnderline(field)).append(",");
                }
                sql.deleteCharAt(sql.length() - 1);
            }
        }
        return sql.toString();
    }

    /**
     * 组装字段集，组装成 xx=?,xx=?,xx=? 格式
     *
     * @param fields
     * @return
     */
    public static String buildFieldsWithQuestion(String[] fields) {
        StringBuffer sql = new StringBuffer();

        if (null != fields && fields.length > 0) {
            for (String field : fields) {
                sql.append(NamingUtil.camelToUnderline(field)).append("=").append("?,");
            }
            sql.deleteCharAt(sql.length() - 1);

        }
        return sql.toString();
    }

    /**
     * 组装字段到？分割 的列表，比如5个字段会组成：?,?,?,?
     *
     * @param fields 字段列表
     * @return
     */
    public static String buildQuestion(String[] fields) {

        String questionList = StringUtils.repeat("?,", fields.length);
        if (questionList.endsWith(",")) {
            questionList = questionList.substring(0, questionList.length() - 1);
        }

        return questionList;
    }

    /**
     * 组装字段到？分割 的列表，比如5个字段会组成：?,?,?,?
     *
     * @param mapItem 字段列表
     * @return
     */
    public static String buildQuestion(Map<String, Object> mapItem) {

        if (MapUtils.isNotEmpty(mapItem)) {

            String questionList = StringUtils.repeat("?,", mapItem.size());
            if (questionList.endsWith(",")) {
                questionList = questionList.substring(0, questionList.length() - 1);
            }

            return questionList;
        }
        return "";
    }

    /**
     * 组装order by语句
     *
     * @param orderby
     * @return
     */
    public static String buildOrderby(LinkedHashMap<String, String> orderby) {
        StringBuffer orderbyql = new StringBuffer();
        if (MapUtils.isNotEmpty(orderby)) {
            orderbyql.append(" order by ");

            for (String key : orderby.keySet()) {
                orderbyql.append(NamingUtil.camelToUnderline(key)).append(" ").append(orderby.get(key)).append(",");
            }
            orderbyql.deleteCharAt(orderbyql.length() - 1);
        }

        return orderbyql.toString();
    }


    /**
     * 获取sql
     *
     * @param page     分页参数，如果为空，则不在sql增加limit ?,?
     * @param orderby  排序参数，如果为空，则不在sql增加ORDER BY
     * @param whereSql 查询条件参数，如果为空，则不在sql增加 and name=?
     * @return sql
     */
    public static String getSql(String tableName, Pager page, String whereSql, Map<String, String> orderby) {
        StringBuffer sql = new StringBuffer("select * from ");
        sql.append(tableName);
        sql.append(" o where 1=1 ");
        if (StringUtils.isNotEmpty(whereSql)) {
            sql.append(" ").append(whereSql);
        }
        if (orderby != null) {
            sql.append(" ORDER BY ");
            for (String string : orderby.keySet()) {
                String value = orderby.get(string);
                if (StringUtils.isEmpty(value)) {
                    value = "ASC";
                }
                sql.append("o.").append(string).append(" ").append(value.toUpperCase()).append(",");
            }
            if (sql.indexOf(",") > -1) {
                sql.deleteCharAt(sql.length() - 1);
            }
        }
        if (page != null) {
            sql.append(" limit ?,? ");
        }
        //System.out.println("------sql.toString()="+sql.toString());
        return sql.toString();
    }


}
