package org.dblink.table;

import com.alibaba.druid.pool.DruidDataSource;
import org.dblink.config.ConnectionConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 管理数据库连接Table的工具类
 * <p>一个table对应一个数据库连接池的连接
 */
public class TableManager {

    private final DruidDataSource dataSource;

    public TableManager(ConnectionConfiguration config) {
        dataSource = new DruidDataSource();
        System.out.println(config);
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
//        dataSource.setMaxActive(config.getMaxActive());
//        dataSource.setInitialSize(config.getInitialSize());
    }

    public <T> Table<T> getTable(Class<T> clazz, String name) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Table<T> table = new DefaultTable<>(clazz, conn, name);

        return table;
    }
}
