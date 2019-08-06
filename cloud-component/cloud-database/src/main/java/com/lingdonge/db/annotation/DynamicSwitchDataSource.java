package com.lingdonge.db.annotation;

import java.lang.annotation.*;

/**
 * 添加注解之后，自动进行多源数据的切换
 * @description： 创建拦截设置数据源的注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicSwitchDataSource {

    String dataSource() default "";
}
