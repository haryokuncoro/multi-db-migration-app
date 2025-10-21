package com.project.migration_runner;

import org.reflections.Reflections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class MigrationRunnerApplication {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(MigrationRunnerApplication.class, args);
        MigrationService service = ctx.getBean(MigrationService.class);

        boolean rollback = args.length > 0 && args[0].equalsIgnoreCase("down");

        // Java migrations
        Reflections reflections = new Reflections("com.project.migration_runner.migrations");
        Set<Class<? extends Migration>> classes = reflections.getSubTypesOf(Migration.class);

        // Sort by version number descending for rollback
        List<Migration> javaMigrations = classes.stream()
                .map(clazz -> {
                    try { return clazz.getDeclaredConstructor().newInstance(); }
                    catch (Exception e) { throw new RuntimeException(e); }
                })
                .sorted((a, b) -> b.getClass().getSimpleName().compareTo(a.getClass().getSimpleName())) // reverse order
                .collect(Collectors.toList());

        // SQL migrations from classpath
        ClassLoader classLoader = MigrationRunnerApplication.class.getClassLoader();
        try (InputStream migrationsList = classLoader.getResourceAsStream("migrations")) {
            // List all folders under resources/migrations
            File migrationsDir = new File(classLoader.getResource("migrations").toURI());
            File[] migrationFolders = migrationsDir.listFiles(File::isDirectory);
            if (migrationFolders != null) {
                Arrays.sort(migrationFolders); // Ensure migrations run in order
                if (rollback){
                    Arrays.sort(migrationFolders, Comparator.comparing(File::getName).reversed()); // descending
                }
                for (File folder : migrationFolders) {
                    String folderName = folder.getName();
                    InputStream upSql = classLoader.getResourceAsStream("migrations/" + folderName + "/up.sql");
                    InputStream downSql = classLoader.getResourceAsStream("migrations/" + folderName + "/down.sql");

                    if (rollback && downSql != null) {
                        System.out.println("Rolling back " + folderName);
                        service.migrateSqlDown(downSql);
                    } else if (!rollback && upSql != null) {
                        System.out.println("Applying " + folderName);
                        service.migrateSqlUp(upSql);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("No SQL migrations found in classpath: migrations");
        }

        // Apply Java migrations
        if (!rollback) {
            service.migrateUp(javaMigrations);
        } else {
            service.migrateDown(javaMigrations);
        }

        System.out.println("All migrations completed!");
        System.exit(0);
    }
}
