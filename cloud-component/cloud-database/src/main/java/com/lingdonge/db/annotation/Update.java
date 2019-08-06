package com.lingdonge.db.annotation;

import java.lang.annotation.*;

/**
 * 带这个注解的Entity才会更新数据
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Update {
}
