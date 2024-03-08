# Demo

A containerized deployment with a demonstration database.

## IDE

The `./.run` directory contains run configurations for both the server connecting to the demo database as well as
the browser application served in continuous update mode.

## Makefile

Contains a demo Postgres database in docker and a config file to use with the server.

<small>***nb*** Requires docker and docker-compose, may require `sudo`.</small>

* Run the demo:

This will start a postgres container, a flyway container that will initialize the database, and a container for the
server application.
```bash
make -C ./demo run
```
This can be run repeatedly; there is no need to stop it, first.

* Start the browser.

Starts the browser in continuous update mode.  This will launch a browser window.

```bash
make run-browser
```

* Stop the demo, destroy the containers:

```bash
make -C ./demo stop
```
