package com.project.migration_runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Service
public class MigrationService {

    private final MigrationDatabase db;
    private final JdbcTemplate jdbcTemplate;

    public MigrationService(
            List<MigrationDatabase> adapters,
            DataSource dataSource,
            @Value("${migration.db}") String dbProfile) { // separate from environment
        this.db = adapters.stream()
                .filter(a -> a.supports(dbProfile))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No adapter for profile " + dbProfile));

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void migrateUp(List<Migration> migrations) throws Exception {
        for (Migration migration : migrations) {
            migration.up(db);
        }
    }

    public void migrateDown(List<Migration> migrations) throws Exception {
        for (int i = migrations.size() - 1; i >= 0; i--) {
            migrations.get(i).down(db);
        }
    }

    public void migrateSqlUp(Path migrationDir) throws Exception {
        MigrationRunner.runUp(migrationDir, jdbcTemplate);
    }

    public void migrateSqlUp(InputStream sqlStream) throws Exception {
        String sql = new String(sqlStream.readAllBytes(), StandardCharsets.UTF_8);
        jdbcTemplate.execute(sql);
    }

    public void migrateSqlDown(Path migrationDir) throws Exception {
        MigrationRunner.runDown(migrationDir, jdbcTemplate);
    }

    public void migrateSqlDown(InputStream sqlStream) throws Exception {
        String sql = new String(sqlStream.readAllBytes(), StandardCharsets.UTF_8);
        jdbcTemplate.execute(sql);
    }
}