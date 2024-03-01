# Kotlin Full Stack Demo

This project implements a server around a database and a web application written entirely in Kotlin.

The web application introspects the target database.

Includes Flyway, Ktor, Kotlin/JS, React, docker-compose, and integration tests with Postgres containers.

| Sub-project                      | Description                                     |
|----------------------------------|-------------------------------------------------|
| [util](./util/README.md)         | Shared code.                                    |
| [kdbc](./kdbc/README.md)         | Light weight JDBC wrapper.                      |
| [domain](./domain/README.md)     | The domain model.                               |
| [database](./database/README.md) | Application database.                           |
| [server](./server/README.md)     | The HTTP API server.                            |
| [browser](./browser/README.md)   | The web application.                            |
| [demo](./demo/README.md)         | A small database for demonstrating the project. |
| [test](./test/README.md)         | Shared code for testing.                        |

## Running

Run the demo.

* Build the project.

`make rebuild`

* Start the database.

`make -C ./demo run`

See the [demo](./demo/README.md) for more details.

* Start the server.

`CONFIG=./demo/config.yml make run-server`

* Start the browser.

Starts the browser in continuous update mode.

`make run-browser`

## Development

* Start the database.

See above.

* Run the server in the IDE.

Use `AppKt` as the main class.
Supply an environmental configuration, as above, or use the `--config` command line option.
A run configuration for IDEA is provided in `/.run`

* Run the browser.

See above.
