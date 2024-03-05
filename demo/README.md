# Demo

A small database for demonstrating the database.

## IDE

The `./.run` directory contains run configurations for both the server connecting to the demo database as well as
the browser application served in continuous update mode.

## Makefile

Contains a demo Postgres database in docker and a config file to use with the server.

<small>***nb*** Requires docker and docker-compose, may require `sudo`.</small>

* Run the demo:

```bash
make -C ./demo run
```

This can be run repeatedly; there is no need to stop it, first.

* Run psql on the database server:

```bash
make -C ./demo psql
```

* Stop the demo, destroy the containers:

```bash
make -C ./demo stop
```

* View the logs on the database server or flyway, respectively.

```bash
make -C ./demo logs demo_pg
make -C ./demo logs flyway
```