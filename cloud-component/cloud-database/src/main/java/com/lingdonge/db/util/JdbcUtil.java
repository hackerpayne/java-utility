package com.lingdonge.db.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JdbcUtil {

    /**
     * 获取@Id注解，得到主键字段名
     *
     * @return
     * @throws Exception
     */
    public static <T> String getPkColumn(Class<T> entityClass) throws Exception {

        String pkColumn = null;
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                Column col = (Column) entityClass.getDeclaredField(field.getName()).getAnnotation(Column.class);
                pkColumn = col.name();
                break;
            }
        }

        return pkColumn;
    }

    /**
     * 获取类上面的Table表名注解，得到表名信息
     *
     * @param entityClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> String getTableName(Class<T> entityClass) throws Exception {
        Table annotation = (Table) entityClass.getAnnotation(Table.class);
        return annotation.name();
    }

    /**
     * 封装list结果
     *
     * @param rs
     * @return
     */
    public static List<String> getColumnLables(ResultSet rs) {
        List<String> columnLables = Lists.newArrayList();
        ResultSetMetaData metaData = null;
        try {
            metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 0; i < count; i++) {
                columnLables.add(metaData.getColumnLabel(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnLables;
    }

    /**
     * ResultSet结果转Map
     *
     * @param rs
     * @return
     */
    public static Map<String, Object> converResultSetToMap(ResultSet rs) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            ResultSetMetaData rm = rs.getMetaData();
            int columnCount = rm.getColumnCount();
            for (int i = 0; i <= columnCount; i++) {
                map.put(rm.getColumnLabel(i), rs.getObject(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

}
