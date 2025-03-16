package top.boking.escore.syn;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class SynDataSourceConfig {

    public static DataSource getSynDataSource(String url, String username, String password) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 连接池配置
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(60000);
        config.setConnectionTimeout(30000);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("syn-data-source-pool");

        return new HikariDataSource(config);
    }
}
