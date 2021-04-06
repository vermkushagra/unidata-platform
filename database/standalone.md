In some cases (e.g. on production) you need to run DB migrations without development project environment

Minimal environment to run DB migrations
1) JDK 8
2) Gradle 2.4+

Configuration
1) Configure path to JDK in gradle.properties
2) Configure DB URL & user account in gradle.properties

Running
0) Run from database folder!
1) run 'gradle -b standalone.gradle <task>'

Where supported tasks are
- flywayBaseline
- flywayClean
- flywayInfo
- flywayInit
- flywayMigrate
- flywayRepair
- flywayValidate

See detailed flyway description: http://flywaydb.org/documentation/

Common process
1) Create DB
2) Upload data from dump
3) Baseline flyway - execute 'gradle -b standalone.gradle flywayBaseline'
4) Migrate DB to latest version - execute 'gradle -b standalone.gradle flywayMigrate'

When receive new migration scripts only migration step is required!