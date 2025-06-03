# kdbc

A minimal ORM for Postgres based on JDBC.

The API is provided as extension functions on JDBC types.
DataSource carries methods for scoping a connection with auto commit on or off.
Connection carries methods for selecting, inserting, and updating records.

The Projection class is a convenience for generating SQL and marshalling domain objects.
It uses reflection to write objects to statements and read objects from result sets.
It also provides methods to generate SQL fragments for common applications, viz.

```
SELECT <columns> FROM <table> AS <alias> ...
```

```
DELETE FROM <table> WHERE ...
```

Note that the delete formula prevents mass deletion by requiring a WHERE clause.

Inserts are handled in batch mode and the SQL is completely generated.

Updates are always a special case (e.g. general updates versus soft deletes).