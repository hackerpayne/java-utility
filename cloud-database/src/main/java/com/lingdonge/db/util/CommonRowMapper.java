package com.lingdonge.db.util;

import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用的一个RowMapper
 */
public class CommonRowMapper implements RowMapper<Object> {

    private Class<?> cl;

    public CommonRowMapper(Class<?> cl) {
        this.cl = cl;
    }

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {

        try {
            Field[] fields = cl.getDeclaredFields();
            Object entity = cl.newInstance();
            for (Field f : fields) {

                //如果结果中没有改field项则跳过
                try {
                    rs.findColumn(f.getName());
                } catch (Exception e) {
                    continue;
                }

                // 修改Field权限
                f.setAccessible(true);
                this.typeMapper(f, entity, rs);

                // 还原Field权限
                f.setAccessible(false);
            }
            return entity;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void typeMapper(Field field, Object obj, ResultSet rs) throws Exception {
        String type = field.getType().getName();

        if (type.equals("java.lang.String") || type.equals("string")) {
            field.set(obj, rs.getString(field.getName()));
        } else if (type.equals("int") || type.equals("java.lang.Integer")) {
            field.set(obj, rs.getInt(field.getName()));
        } else if (type.equals("long") || type.equals("java.lang.Long")) {
            field.set(obj, rs.getLong(field.getName()));
        } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            field.set(obj, rs.getBoolean(field.getName()));
        } else if (type.equals("java.token.Date")) {
            field.set(obj, rs.getDate(field.getName()));
        }

    }

}