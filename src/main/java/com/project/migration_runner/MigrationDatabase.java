package com.project.migration_runner;

import java.util.Map;

public interface MigrationDatabase {
    void execute(String sqlOrCommand) throws Exception;
    void createTable(String tableName, Map<String, String> columns) throws Exception;
    void dropTable(String tableName) throws Exception;
    boolean tableExists(String tableName) throws Exception;
    boolean supports(String profile); // used to select DB adapter
}
