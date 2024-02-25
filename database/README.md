# database

An ORM for the domain based on `jdbc`.

The `Database` class builds on a handful of functions for managing connections, statements, and result sets.

Implementing a select requires an SQL statement, positional parameters, and a function to map a result set to a domain
object.
This example has one positional parameter and a lambda to map a result set to a `User` object (which object is assumed).

```kotlin
context(Database)
suspend fun listUsers(domainName: String): List<User> =
    select(
        "SELECT id, name FROM users WHERE domain = ?",
        domainName
    ) {
        it.run {
            User(rs.getInt(1), rs.getString(2))
        }
    }
```

For convenience, the Marshaller interface groups column names for projections together with a function to map a result
set to a domain object.
Implementing this for the `User` object would look like this:

```kotlin
object UserMarshaller : Marshaller<User> {
    override val columns = listOf("id", "name")
    override fun read(rs: ResultSet) = User(rs.getInt(1), rs.getString(2))
}
```

Using a marshaller in a select requires invoking it's read method, thus:

For even more convenience, but at a run time penalty, a `GenericMarshaller` is provided to map a result set to a domain
object using reflection.
Implementing this for the `User` object would look like this:

```kotlin
val userMarshaller = GenericMarshaller<User>(fieldNameConverter)
```

The fieldNameConverter is used to infer projection field names from the parameter names of the domain object's primary
constructor.
This could be implemented using a map.
If you're lucky, you might be able to use a case conversion function.
This example uses guava.

```kotlin
val fieldNameConverter = object : FieldNameConverter {
    override fun toColumnName(name: String): String =
        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
}
``` 
