package com.xzh.datasource;

import com.xzh.constants.DataSourceConstants;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, LogDataSourceProperties.class})
public class DynamicDataSourceConfig {

    // 1. 创建主库数据源
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource masterDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    // 2. 创建日志库数据源
    @Bean
    public DataSource logDataSource(DataSourceProperties masterProperties, LogDataSourceProperties logProperties) {
        // 如果没有配置日志库 URL，则自动根据主库 URL 替换数据库名
        String url = logProperties.getUrl();
        if (url == null || url.isEmpty()) {
            String masterUrl = masterProperties.getUrl();
            // 简单的逻辑：替换 URL 中的数据库名部分
            url = masterUrl.replaceFirst("/[^/?]+(\\?|$)", "/" + logProperties.getDbName() + "$1");
        }
        return masterProperties.initializeDataSourceBuilder()
                .url(url)
                .username(logProperties.getUsername() != null ? logProperties.getUsername() : masterProperties.getUsername())
                .password(logProperties.getPassword() != null ? logProperties.getPassword() : masterProperties.getPassword())
                .build();
    }

    // 3. 核心：组装动态路由数据源
    @Bean
    @Primary
    public DataSource dataSource(DataSource masterDataSource, DataSource logDataSource) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceConstants.MASTER, masterDataSource);
        targetDataSources.put(DataSourceConstants.LOG, logDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        return routingDataSource;
    }

    // 4. 配置 JdbcTemplate 供 ServiceLogWriter 使用
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
