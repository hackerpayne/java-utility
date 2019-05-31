package com.lingdonge.db.util;

import com.lingdonge.core.reflect.NamingUtil;

/**
 * 默认名称处理handler
 * <p>
 * https://www.dexcoder.com/selfly/article/430
 */
public class DefaultNameHandler implements NameHandler {
    /**
     * 字段前缀
     */
    private static final String PREFIX = "_";
    /**
     * 主键后缀
     */
    private static final String PRI_SUFFIX = "_id";

    /**
     * 根据实体名获取表名
     *
     * @param entityName
     * @return
     */
    @Override
    public String getTableName(String entityName) {
        //Java属性的骆驼命名法转换回数据库下划线“_”分隔的格式
        return NamingUtil.camelToUnderline(entityName);
    }

    /**
     * 根据表名获取主键名
     *
     * @param entityName
     * @return
     */
    @Override
    public String getPrimaryName(String entityName) {
        String underlineName = NamingUtil.camelToUnderline(entityName);
        //正如前面说到的，数据库列名统一以“_”开始，主键以表名加上“_id” 如user表主键即“_user_id”
        return PREFIX + underlineName + PRI_SUFFIX;
    }

    /**
     * 根据属性名获取列名
     *
     * @param fieldName
     * @return
     */
    @Override
    public String getColumnName(String fieldName) {
        String underlineName = NamingUtil.camelToUnderline(fieldName);
        //数据库列名统一以“_”开始
        return PREFIX + underlineName;
    }
}