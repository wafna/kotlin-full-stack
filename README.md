# DB Explorer.

A server that provides a web interface to explore a database.

## Demo

Contains a demo Postgres database in docker and a config file to use with the server.

<small>***nb*** Requires docker and docker-compose, may require `sudo`.</small>

* Run the demo:
```bash
make -C demo run
```
This can be run repeatedly; there is no need to stop it, first.

* Run psql on the database server:
```bash
make -C demo psql
```

* Stop the demo, destroy the containers:
```bash
make -C demo stop
```