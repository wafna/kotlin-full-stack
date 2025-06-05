# kdbc

This is a minimal ORM providing a functional interfaces around JDBC objects.
It avoids vendor specificity by avoiding abstractions around SQL and data.

The Entity class is a convenience for generating SQL and marshalling domain objects.
It provides methods to generate SQL fragments for common applications, viz.
Use of the Entity class is optional.

```
SELECT <columns> FROM <table> AS <alias> ...
```

```
DELETE FROM <table> WHERE ...
```

Note that the delete formula prevents mass deletion by requiring a WHERE clause.

Inserts are handled in batch mode and the SQL is completely generated.

Updates are always a special case (e.g. general updates versus soft deletes).