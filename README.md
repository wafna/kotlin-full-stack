# DB Explorer.

A server that provides a web interface to explore a database.

| Sub-project                      | Description                                     |
|----------------------------------|-------------------------------------------------|
| [util](./util/README.md)         | Shared code.                                    |
| [database](./database/README.md) | Generic JDBC wrapper.                           |
| [domain](./domain/README.md)     | The domain model.                               |
| [db](./db/README.md)             | The application database model.                 |
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

* Run the browser.

See above.
