package com.project.migration_runner;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MigrationRunner {

    public static void runUp(Path migrationDir, JdbcTemplate jdbcTemplate) throws IOException {
        Path upFile = migrationDir.resolve("up.sql");
        if (Files.exists(upFile)) {
            jdbcTemplate.execute(Files.readString(upFile));
        }
    }

    public static void runDown(Path migrationDir, JdbcTemplate jdbcTemplate) throws IOException {
        Path downFile = migrationDir.resolve("down.sql");
        if (Files.exists(downFile)) {
            jdbcTemplate.execute(Files.readString(downFile));
        }
    }
}