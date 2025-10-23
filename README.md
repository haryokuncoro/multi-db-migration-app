# Migration Runner

A lightweight Java migration runner similar to Flyway. Supports both SQL and Java migrations with rollback and migration history tracking.

---

## Project Structure

```

src/main/resources/migrations/
├── V001__create_users_table/
│   ├── up.sql
│   └── down.sql
├── V002__add_email_to_users/
│   ├── up.sql
│   └── down.sql

```

Java migrations are located in:

```

com.project.migration_runner.migrations

````

Each Java migration implements the `Migration` interface with `up()` and `down()` methods.

---

## Running Migrations

### 1. Build the project

```bash
./gradlew clean build
````

### 2. Run migrations

#### Default (applies all migrations)

```bash
./gradlew run
```

#### Specify Spring profile (e.g., `mysql` or `postgres`)

```bash
./gradlew run -Dspring.profiles.active=mysql
```

#### Rollback last migration

```bash
./gradlew run -Dspring.profiles.active=mysql --args='rollback 1'
```

#### Rollback multiple steps

```bash
./gradlew run -Dspring.profiles.active=mysql --args='rollback 3'
```

---

## Notes

1. SQL migrations should have both `up.sql` and `down.sql` in their versioned folder.
2. Java migrations must implement the `Migration` interface and provide `up()` and `down()` methods.
3. Migration history is stored in the database table `migration_history`.
4. Rollback will execute migrations in **reverse order** of application.

---

## Example

Apply all migrations for MySQL profile:

```bash
./gradlew run -Dspring.profiles.active=mysql
```

Rollback last 2 migrations:

```bash
./gradlew run -Dspring.profiles.active=mysql --args='rollback 2'
```

```
