package com.lingdonge.db.jpa.rowmapper;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import com.lingdonge.core.reflect.BeanUtil;
import com.lingdonge.db.util.NameHandler;
import org.springframework.jdbc.core.RowMapper;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 默认通用类型映射转换:将Jdbc查询出来的ResultSet转换为Entity实体类
 * http://blog.csdn.net/phantomes/article/details/37880009
 */
public class DefaultRowMapper implements RowMapper<Object> {

    /**
     * 转换的目标对象
     */
    private Class<?> clazz;

    /**
     * 名称处理器
     */
    private NameHandler nameHandler;

    public DefaultRowMapper(Class<?> clazz, NameHandler nameHandler) {
        this.clazz = clazz;
        this.nameHandler = nameHandler;
    }

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        Object entity = ReflectUtil.newInstance(this.clazz);
        BeanInfo beanInfo = BeanUtil.getBeanInfo(this.clazz);
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            String column = nameHandler.getColumnName(pd.getName());
            Method writeMethod = pd.getWriteMethod();
            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
            }
            try {
                writeMethod.invoke(entity, resultSet.getObject(column));
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        return entity;
    }
}