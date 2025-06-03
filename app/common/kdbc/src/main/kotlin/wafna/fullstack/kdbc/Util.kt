package wafna.fullstack.kdbc

import java.sql.Array
import java.sql.ResultSet
import java.sql.Timestamp

fun inList(count: Int): String = "(${List(count) { "?" }.joinToString(", ")})"

/** Enforces that the result of a SELECT contains no more that one record. */
val <T> List<T>.optional: T?
    get() =
        when (size) {
            0 -> null
            1 -> first()
            else -> throw IllegalArgumentException("Multiple results received.")
        }

/**
 * Assert the number of records affected by an UPDATE.
 */
fun Int.requireUpdates(count: Int) = require(this == count) { "Expected $count updates, actual $this." }

/**
 * Assert the number of records affected by an INSERT.
 */
fun IntArray.requireInserts(count: Int) = sum().let { require(it == count) { "Expected $count updates, actual $it." } }

/**
 * Convenience class for reading the fields from a ResultSet in fixed order, e.g. the declared field
 * order in an Entity. This also makes the nullability of data from the ResultSet more explicit.
 */
class ResultSetFieldIterator(val rs: ResultSet) : ResultSet by rs {
    private var position = 0
    val next: Int
        get() = ++position
}

fun ResultSetFieldIterator.getInt(): Int? = getInt(next)

fun ResultSetFieldIterator.getString(): String? = getString(next)

fun ResultSetFieldIterator.getObject(): Any? = getObject(next)

fun ResultSetFieldIterator.getTimestamp(): Timestamp? = getTimestamp(next)

fun ResultSetFieldIterator.getArray(): Array? = getArray(next)

@Suppress("UNCHECKED_CAST")
fun ResultSetFieldIterator.getStringList(): List<String>? = (getArray()?.array as kotlin.Array<String>).toList()

/** Read a single record from a result set using a field iterator function. */
inline fun <R> ResultSet.readRecord(run: ResultSetFieldIterator.() -> R): R = ResultSetFieldIterator(this).run()

/** Read all the records from a result set using a field iterator function. */
inline fun <R> ResultSet.readRecords(run: ResultSetFieldIterator.() -> R): List<R> =
    buildList {
        while (next()) add(readRecord(run))
    }

/** This is used to produce param lists from source records on demand. */
fun <P, Q> Iterable<P>.transformer(f: (P) -> Q): Iterator<Q> {
    val it = iterator()
    return object : Iterator<Q> {
        override fun next(): Q = f(it.next())

        override fun hasNext(): Boolean = it.hasNext()
    }
}
