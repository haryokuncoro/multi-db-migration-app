package com.project.migration_runner.adapters;

import com.project.migration_runner.MigrationDatabase;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MongoMigrationDatabase implements MigrationDatabase {

    private final MongoTemplate mongoTemplate;

    public MongoMigrationDatabase(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void execute(String command) {
        // Optional: parse or run raw commands
    }

    @Override
    public void createTable(String tableName, Map<String, String> columns) {
        if (!mongoTemplate.collectionExists(tableName)) {
            mongoTemplate.createCollection(tableName);
        }
    }

    @Override
    public void dropTable(String tableName) {
        mongoTemplate.dropCollection(tableName);
    }

    @Override
    public boolean tableExists(String tableName) {
        return mongoTemplate.collectionExists(tableName);
    }

    @Override
    public boolean supports(String profile) {
        return profile.equalsIgnoreCase("mongo");
    }
}