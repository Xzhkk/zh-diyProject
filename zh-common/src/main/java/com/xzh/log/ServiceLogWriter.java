package com.xzh.log;

import com.xzh.annotation.TargetDataSource;
import com.xzh.constants.DataSourceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceLogWriter {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Set<String> initializedTables = ConcurrentHashMap.newKeySet();

    @TargetDataSource(DataSourceConstants.LOG) // 强制在日志库执行
    public void ensureTableExists(String tableName) {
        if (initializedTables.contains(tableName)) return;
        // 执行建表 SQL
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                "  `id` varchar(256) NOT NULL COMMENT '主键'," +
                "  `url` varchar(255) DEFAULT NULL COMMENT '请求接口路径'," +
                "  `create_time` datetime DEFAULT NULL COMMENT '请求时间'," +
                "  `result` varchar(255) DEFAULT NULL COMMENT '请求结果'," +
                "  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        jdbcTemplate.execute(sql);
        initializedTables.add(tableName);
    }
}
