package com.project.migration_runner.adapters;

import com.project.migration_runner.MigrationDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SqlMigrationDatabase implements MigrationDatabase {

    private final JdbcTemplate jdbcTemplate;

    public SqlMigrationDatabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void execute(String sql) throws Exception {
        // Make ALTER TABLE safer by replacing "DROP COLUMN" with IF EXISTS
        // This is simple string replacement; adjust based on your migration SQL
        String safeSql = sql.replaceAll("(?i)DROP COLUMN (\\w+)", "DROP COLUMN IF EXISTS $1");
        jdbcTemplate.execute(safeSql);

    }

    @Override
    public void createTable(String tableName, Map<String, String> columns) throws Exception {
        String cols = columns.entrySet()
                .stream()
                .map(e -> e.getKey() + " " + e.getValue())
                .collect(Collectors.joining(", "));
        jdbcTemplate.execute("CREATE TABLE " + tableName + " (" + cols + ")");
    }

    @Override
    public void dropTable(String tableName) throws Exception {
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
    }

    @Override
    public boolean tableExists(String tableName) throws Exception {
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM " + tableName + " LIMIT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean supports(String profile) {
        return profile.equalsIgnoreCase("mysql") || profile.equalsIgnoreCase("postgres");
    }
}
