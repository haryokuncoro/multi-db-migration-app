package com.project.migration_runner;

public interface Migration {
    void up(MigrationDatabase db) throws Exception;
    void down(MigrationDatabase db) throws Exception;
}
