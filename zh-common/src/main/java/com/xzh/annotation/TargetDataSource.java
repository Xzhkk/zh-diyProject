package com.xzh.annotation;

import com.xzh.constants.DataSourceConstants;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value() default DataSourceConstants.MASTER; // 默认为主库
}
