package com.project.migration_runner.migrations;

import com.project.migration_runner.Migration;
import com.project.migration_runner.MigrationDatabase;

import java.util.Map;

public class V003__java_migration_example implements Migration {

    @Override
    public void up(MigrationDatabase db) throws Exception {
        // Example: create a table
        db.createTable("example_table", Map.of(
                "id", "BIGINT PRIMARY KEY AUTO_INCREMENT",
                "name", "VARCHAR(255)"
        ));
        System.out.println("V003__java_migration_example up executed!");
    }

    @Override
    public void down(MigrationDatabase db) throws Exception {
        // Rollback: drop the table
        db.dropTable("example_table");
        System.out.println("V003__java_migration_example down executed!");
    }
}