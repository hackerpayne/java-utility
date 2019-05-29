package com.lingdonge.db.extension;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 我们通过实现自己的LanguageDriver，在MyBatis编译语句前，将我们自定义的标签替换为了动态SQL语句，其等同于：
 * 通过实现LanguageDriver，剥离出了冗长的动态SQL语句，简化Select In的注解代码。
 * <p>
 * 使用方法：
 *
 * @Lang(SimpleSelectInExtendedLanguageDriver.class)
 * @Select("SELECT * FROM users WHERE id IN (#{userIds})")
 * List<User> selectUsers(@Param("userIds") List<String> userIds);
 */
public class SimpleSelectInExtendedLanguageDriver extends XMLLanguageDriver implements LanguageDriver {

    private final Pattern inPattern = Pattern.compile("\\(#\\{(\\w+)\\}\\)");

    @Override
    public SqlSource createSqlSource(Configuration configuration,
                                     String script, Class<?> parameterType) {

        Matcher matcher = inPattern.matcher(script);
        if (matcher.find()) {
//            script = matcher.replaceAll("(<foreach collection=\"$1\" item=\"__item\" separator=\",\" >#{__item}</foreach>)");
            script = matcher.replaceAll("<foreach collection=\"$1\" item=\"_item\" open=\"(\" separator=\",\" close=\")\" >#{_item}</foreach>");
        }

        script = "<script>" + script + "</script>";
        return super.createSqlSource(configuration, script, parameterType);
    }
}
