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

The advantages of fine-grained projects are many.

- Enhance testability
- Promote code reuse
- Control dependencies
- Reduce build and test times
- Promote single responsibility

Gradle's plug-in system makes this exceedingly convenient by allowing common project configuration to be shared across projects.

This project, for example, splits the CRUD operations on the database into their own project (*db*).  
The *api* project composes these operations inside transactions.
This gives a finer grain for testing as well providing maximum flexibility and reuse for implementing the API.

Furthermore, the project treats the API as an embeddable component; 
the HTTP interface is simply one presentation of the API designed specifically for the browser client.
Thus, the functions of persistence, business logic, and network transport are split into their
own subprojects.
This organization allows for easy testing and instrumentation at all levels as well as 
offering a convenient way to provide multiple external interfaces.

A further benefit of this organization is that the domain becomes extensible.
The API layer adds its own domain objects, built upon the core business domain,
according to the shape of information it would like to transmit.
The HTTP layer adds yet more domain objects for the same reason.
In this manner, the business API and presentation API for the web client are decoupled.

***Exceptions and Boundaries***

This project employs a theory of exception handling based on boundaries in code.
Attempting to wrap return values has its place, but can never be complete in the presence
of all thrown exceptions and has some drawbacks, namely syntactic verbosity and leakage of
side effects when the return value is not needed but the result not bound.

To address this, the project defines boundaries where all exceptions of any sort are resolved.
These boundaries are the (very few) places in code where we need to guarantee a result. 
One such place is at the API interface.
Its methods must open transactions to obtain connections.
The `transact` method provides an exception boundary, guaranteeing a *Result*.
The HTTP routes are an obvious boundary, as well.

### The Stack

* Postgresql
* Flyway
* Ktor
* React
* Let's Plot

#### Supporting Tech

* Gradle
* Docker

## Fullstack

The application demonstrates importing CSV files and displaying their data in
charts.