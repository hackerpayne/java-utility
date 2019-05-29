package com.lingdonge.db.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 查询满足条件所有数据（并翻页）
 */
public class SelectNewPageInjector extends AbstractMethod {


    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sqlScript = "<script>\n select a.* from %s as a join (select %s from %s %s order by %s asc limit 100,100) as tmp on tmp.%s=a.%s\n</script>";
        String sqlMethod = "queryPages";
        String sql = String.format(sqlScript, tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getTableName(), sqlWhereEntityWrapper(true, tableInfo), tableInfo.getKeyColumn(), tableInfo.getKeyColumn(), tableInfo.getKeyColumn());

        if (tableInfo.getTableName().contains("channel_user_info")){
            System.out.println("sql语句为：" + sql);
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);

//        return this.addMappedStatement(mapperClass, sqlMethod, sqlSource, tableInfo);
        /* 返回 resultMap 映射结果集 */
        return this.addSelectMappedStatement(mapperClass, sqlMethod, sqlSource, modelClass, tableInfo);
//        return addSelectMappedStatement(mapperClass,sqlMethod,sqlSource,modelClass,tableInfo);
//        return addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.SELECT, null,
//                null, tableInfo, new NoKeyGenerator(), null, null);
    }
}
