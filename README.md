# Kotlin Full Stack Demo

This project implements a server around a database and a web application written entirely in Kotlin.

The web application introspects the target database.

Includes Flyway, Ktor, Kotlin/JS, React, docker-compose, and integration tests with Postgres containers.

| Sub-project                                | Description                                     |
|--------------------------------------------|-------------------------------------------------|
| [common/logger](./common/logger/README.md) | Lazy logger.                                    |
| [common/kdbc](./common/kdbc/README.md)     | Light weight JDBC wrapper.                      |
| [common/test](./common/test/README.md)     | Shared code for testing.                        |
| [app/domain](./app/domain/README.md)       | The domain model.                               |
| [app/database](./app/database/README.md)   | Application database.                           |
| [app/server](./app/server/README.md)       | The HTTP API server.                            |
| [app/browser](./app/browser/README.md)     | The web application.                            |
| [demo](./demo/README.md)                   | A small database for demonstrating the project. |

## Notes

* Small project lead to easier dependency management and faster build times.