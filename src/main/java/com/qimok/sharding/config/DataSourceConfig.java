package com.qimok.sharding.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

/**
 * @author qimok
 * @since 2020-08-20
 */
@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "getNoShardingDataSource")
    @Qualifier("getNoShardingDataSource")
    public DataSource getNoShardingDataSource(@Autowired NoShardingDataSourceConfig noShardingDataSourceConfig) {
        DataSource dataSource = DataSourceBuilder.create()
                .url(noShardingDataSourceConfig.jdbcUrl)
                .username(noShardingDataSourceConfig.username)
                .password(noShardingDataSourceConfig.password)
                .driverClassName(noShardingDataSourceConfig.driverClassName)
                .build();
        return new TransactionAwareDataSourceProxy(dataSource);
    }

    @Bean(name = "noShardingTransactionManager")
    @Primary
    public PlatformTransactionManager noShardingTransactionManager(@Qualifier("getNoShardingDataSource")
                                                                           DataSource getNoShardingDataSource) {
        return new DataSourceTransactionManager(getNoShardingDataSource);
    }

    @Primary
    @Bean(name = "noShardDsl")
    @Qualifier("noShardDsl")
    public DSLContext noShardDsl(@Qualifier("getNoShardingDataSource") DataSource getNoShardingDataSource) {
        return DSL.using(new DefaultConfiguration()
                .set(getNoShardingDataSource)
                .set(SQLDialect.MYSQL));
    }

    @Bean(name = "getShardingDataSource")
    @Qualifier("getShardingDataSource")
    public DataSource shardingDataSource(@Qualifier("shardingDataSource") DataSource shardingDataSource) {
        return new TransactionAwareDataSourceProxy(shardingDataSource);
    }

    @Bean(name = "shardingTransactionManager")
    public PlatformTransactionManager shardingTransactionManager(@Qualifier("getShardingDataSource")
                                                                             DataSource getShardingDataSource) {
        return new DataSourceTransactionManager(getShardingDataSource);
    }


    @Bean(name = "shardDsl")
    @Qualifier("shardDsl")
    public DSLContext shardDsl(@Qualifier("getShardingDataSource") DataSource getShardingDataSource) {
        return DSL.using(new DefaultConfiguration()
                .set(getShardingDataSource)
                .set(new Settings().withRenderSchema(false))
                .set(SQLDialect.MYSQL));
    }

}
