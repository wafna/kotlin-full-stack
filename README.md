# Kotlin Full Stack Demo

This project implements a server around a database and a web application written entirely in Kotlin.

The goal is to demonstrate various techniques in Kotlin and Gradle as well as explore a multi-project
organization with the following goals:

- Maximum testability and refactorability.
- Maximum code reuse in build and source.
- Minimum build and test times.
- Control of dependencies.
- Gild the Road

This last point refers to the North Star of this exercise:

> Make the right thing the easiest thing.

This operationalizes in many ways but, mainly, it involves ensuring
that when maintaining, extending, or refactoring the code base, 
the established methods and patterns are followed.
To give a concrete example, adding a new domain entity,
which would involve a database migration and changes all the way up
to the HTTP routes, should be a straightforward, reliable, and 
relatively simple task.
This is achieved by having 
- Plenty of examples in the code base.
- Good abstractions that make the new code as lightweight and non-repetitive as possible.
- A fine-grained project structure so that each change up the stack is well-defined and localized.

These goals and techniques are synergistic, reinforcing each other in many ways.
For example, good abstractions do a lot of heavy lifting.
Not only do they reduce the amount of code, but they also improve the refactorability of the code as well as guiding
developers along the right path.

***Fine Grained Projects***

Everyone from Sun Tzu to Edsger Dijkstra agrees; divide and conquer is an effective strategy.

The advantages of fine-grained projects are many and Gradle's plug-in system makes this exceedingly convenient by
allowing common project configuration to be shared across projects.

- Enhance Testability
- Promote Code Reuse
- Control Third Party Dependencies
- Reduce Build and Test Times
- Promote Single Responsibility

Project structure should be factored just like code; by extracting bits to be shared as one would in any language.
In Gradle, this happens at both the project and plug-in levels.
In addition to sharing code, this reduces build and test times by having less code around modified code.

More abstractly, small projects promote single responsibility.
Here are two examples as implemented in this project.

This project splits the CRUD operations on the database into their own project.  
The *api* project composes these operations inside transactions.
This gives a finer grain for testing as well providing maximum flexibility and reuse for implementing the API.

One more!

<hr/>
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

Expand on:

Confusion of domain layers.  Don't merge the core business domain with objects convenient for the API or the browser.
It should be only the objects the DB layer (or core business persistence) understands.

Confusion of HTTP interface for API boundary: it works out better if the API is embeddable and the HTTP layer is
simply a presentation layer, maybe for a browser, maybe a REST API for public consumption.  Write two!