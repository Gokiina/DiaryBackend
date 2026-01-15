package com.app.Diary.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        // Log available environment variables (keys only) for debugging
        System.out.println("DEBUG: Listing available environment variables:");
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            if (entry.getKey().contains("URL") || entry.getKey().contains("DB")) {
                 System.out.println(" - " + entry.getKey() + " = [HIDDEN]");
            }
        }

        String dbUrl = env.getProperty("SPRING_DATASOURCE_URL");
        if (!StringUtils.hasText(dbUrl)) {
            dbUrl = env.getProperty("DATABASE_URL");
        }
        if (!StringUtils.hasText(dbUrl)) {
            dbUrl = env.getProperty("JDBC_DATABASE_URL");
        }

        if (!StringUtils.hasText(dbUrl)) {
            System.err.println("WARNING: No Database URL found in environment variables. Falling back to H2 in-memory database.");
            HikariConfig h2Config = new HikariConfig();
            h2Config.setJdbcUrl("jdbc:h2:mem:diarydb;DB_CLOSE_DELAY=-1");
            h2Config.setDriverClassName("org.h2.Driver");
            h2Config.setUsername("sa");
            h2Config.setPassword("");
            return new HikariDataSource(h2Config);
        }

        System.out.println("Found Database URL. Configuring PostgreSQL DataSource...");

        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        if (!StringUtils.hasText(username)) {
            username = env.getProperty("DATABASE_USERNAME");
        }

        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");
        if (!StringUtils.hasText(password)) {
            password = env.getProperty("DATABASE_PASSWORD");
        }

        String jdbcUrl = dbUrl;

        // Fix for Render/Heroku postgres:// URLs
        if (dbUrl.startsWith("postgres://")) {
            jdbcUrl = "jdbc:postgresql://" + dbUrl.substring("postgres://".length());
        } else if (dbUrl.startsWith("postgresql://")) {
             jdbcUrl = "jdbc:postgresql://" + dbUrl.substring("postgresql://".length());
        }

        // Parse user info from URL if present (postgres://user:pass@host...)
        if (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://")) {
            try {
                URI uri = new URI(dbUrl);
                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":");
                    if (userInfo.length >= 1 && !StringUtils.hasText(username)) {
                        username = userInfo[0];
                    }
                    if (userInfo.length >= 2 && !StringUtils.hasText(password)) {
                        password = userInfo[1];
                    }

                    // Reconstruct clean JDBC URL
                    String host = uri.getHost();
                    int port = uri.getPort();
                    String path = uri.getPath();
                    String query = uri.getQuery();

                    jdbcUrl = "jdbc:postgresql://" + host + (port != -1 ? ":" + port : "") + path;
                    if (query != null) {
                        jdbcUrl += "?" + query;
                    }
                }
            } catch (URISyntaxException e) {
                System.err.println("Warning: Could not parse database URL for user info: " + e.getMessage());
            }
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);

        if (StringUtils.hasText(username)) {
            config.setUsername(username);
        }
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }

        return new HikariDataSource(config);
    }
}
