package com.qimok.sharding.aop.annotation;

import java.lang.annotation.*;

/**
 * 多数据源事务注解
 *
 * @author qimok
 * @since 2020-08-20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiDataSourceTransactional {

    /**
     * 事务管理器数组
     */
    String[] transactionManagers();
}
