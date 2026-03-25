package com.xzh.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "xzh.datasource.log")
public class LogDataSourceProperties {
    private boolean enabled = false;
    private String dbName;
    private String url;
    private String username;
    private String password;
}
