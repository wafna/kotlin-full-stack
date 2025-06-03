package wafna.fullstack.kdbc

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

/** Expresses setting a parameter into a prepared statement. */
typealias Param = (statement: PreparedStatement, parameterIndex: Int) -> Unit

val Null: Param = { statement, parameterIndex -> statement.setNull(parameterIndex, Types.NULL) }

/** Explicitly sets NULL for null receivers. */
fun <T> T?.nullableParam(putter: (PreparedStatement, Int, T) -> Unit): Param =
    if (null == this) Null
    else { statement, parameterIndex -> putter(statement, parameterIndex, this) }

val Int?.paramInt: Param
    get() = nullableParam { s, i, x -> s.setInt(i, x) }

val String?.paramString: Param
    get() = nullableParam { s, i, x -> s.setString(i, x) }

val Instant?.paramInstant: Param
    get() =
        nullableParam { s, i, x ->
            val zone = ZoneId.of(Calendar.getInstance().timeZone.id)
            val dateTime = LocalDateTime.ofInstant(x.toJavaInstant(), zone)
            s.setTimestamp(i, Timestamp.valueOf(dateTime))
        }

val Boolean?.paramBoolean: Param
    get() = nullableParam { s, i, x -> s.setBoolean(i, x) }

fun Iterable<String>.paramStrings(connection: Connection): Param =
    nullableParam { s, i, x ->
        val array = x.toList().toTypedArray()
        s.setArray(i, connection.createArrayOf("TEXT", array))
    }

val Any?.paramAny: Param
    get() = nullableParam { s, i, x -> s.setObject(i, x) }
