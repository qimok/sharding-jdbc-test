package com.qimok.sharding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author qimok
 * @since 2020-08-20
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.noshardingds")
public class NoShardingDataSourceConfig {

    public String jdbcUrl;
    public String username;
    public String password;
    public String driverClassName;

}
