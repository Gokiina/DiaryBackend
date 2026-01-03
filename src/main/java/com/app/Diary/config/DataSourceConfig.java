package com.app.Diary.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Value("${SPRING_DATASOURCE_URL:${DATABASE_URL:${JDBC_DATABASE_URL:}}}")
    private String dbUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:${DATABASE_USERNAME:}}")
    private String dbUsername;

    @Value("${SPRING_DATASOURCE_PASSWORD:${DATABASE_PASSWORD:}}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        if (!StringUtils.hasText(dbUrl)) {
            // Fallback or error? Let's log and let it fail naturally or return null
            System.err.println("CRITICAL: No Database URL found in environment variables (SPRING_DATASOURCE_URL, DATABASE_URL, JDBC_DATABASE_URL).");
            // We can try to proceed if maybe autoconfig picks something up, but likely will fail.
        }

        String jdbcUrl = dbUrl;
        String username = dbUsername;
        String password = dbPassword;

        // Fix for Render/Heroku postgres:// URLs
        if (dbUrl != null && dbUrl.startsWith("postgres://")) {
            jdbcUrl = "jdbc:postgresql://" + dbUrl.substring("postgres://".length());
        } else if (dbUrl != null && dbUrl.startsWith("postgresql://")) {
             jdbcUrl = "jdbc:postgresql://" + dbUrl.substring("postgresql://".length());
        }

        // If the URL contains user info (postgres://user:pass@host:port/db), we might need to extract it
        // because sometimes the JDBC driver or connection pool prefers explicit user/pass properties.
        // However, the Postgres JDBC driver generally supports user/pass in the URL.
        // But to be safe and robust (especially if username/password vars are empty), let's parse it.

        if (dbUrl != null && (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://"))) {
            try {
                // Use URI to parse user info
                // We need to use "http" scheme temporarily to make URI class happy if "postgres" isn't recognized?
                // Actually URI handles generic schemes.
                URI uri = new URI(dbUrl);
                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":");
                    if (username == null || username.isEmpty()) {
                        username = userInfo[0];
                    }
                    if (userInfo.length > 1 && (password == null || password.isEmpty())) {
                        password = userInfo[1];
                    }
                    // Reconstruct JDBC URL without user info for cleaner separation, OR keep it.
                    // The driver supports it in URL, but removing it ensures we use the explicit properties.
                    // jdbc:postgresql://host:port/path
                    String host = uri.getHost();
                    int port = uri.getPort();
                    String path = uri.getPath();
                    jdbcUrl = "jdbc:postgresql://" + host + (port != -1 ? ":" + port : "") + path;
                }
            } catch (URISyntaxException e) {
                System.err.println("Warning: Could not parse database URL for user info: " + e.getMessage());
                // Fallback to using the converted JDBC URL as is
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
