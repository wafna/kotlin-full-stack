package wafna.fullstack.kdbc

import io.kotest.assertions.fail
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.UUID
import javax.sql.DataSource
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test

enum class DenizenType(val sql: String) {
    Knight("knight"),
    Knave("knave"),
}

data class Denizen(
    val id: UUID,
    val name: String?,
    val index: Int?,
    val denizenType: DenizenType?,
    val created: Instant?,
)

// Create the schema for our testing database.
suspend fun DataSource.initTestDB() {
    runTransaction {
        update("CREATE TYPE denizen_types AS ENUM ('knight', 'knave')")
        update(
            """CREATE TABLE denizens (
              |  id UUID NOT NULL,
              |  PRIMARY KEY (id),
              |  name TEXT NOT NULL,
              |  index INTEGER,
              |  denizen_type denizen_types,
              |  created_at  TIMESTAMP NOT NULL,
              |  deleted_at  TIMESTAMP DEFAULT NULL
              |)"""
                .trimMargin(),
        )
    }
}

// The nanos resolution of the timestamp gets lost in the round trip to the database
// so we truncate them immediately.
fun Instant.dropNanos(): Instant = Instant.fromEpochMilliseconds(toEpochMilliseconds())

fun Timestamp.toKotlinInstant(): Instant = Instant.fromEpochMilliseconds(time)

// Custom getters...

fun ResultSetFieldIterator.getUUID(): UUID? = getObject() as UUID

fun ResultSetFieldIterator.getInstant(): Instant? = getTimestamp()?.toKotlinInstant()

fun ResultSetFieldIterator.getDenizenType(): DenizenType? =
    getString()?.let {
        when (it) {
            DenizenType.Knight.sql -> DenizenType.Knight
            DenizenType.Knave.sql -> DenizenType.Knave
            else -> fail("Unknown denizen type: $it")
        }
    }

// Custom setters...

// This masks the Any? receiver that would otherwise mishandle it.
// UUID does get handled as Any but the driver understands this type.
val DenizenType?.param: Param
    get() =
        nullableParam { statement, parameterIndex, value ->
            statement.setString(parameterIndex, value.sql)
        }

fun withTestDB(borrow: suspend (DataSource) -> Unit) =
    runBlocking {
        withPGDB { db ->
            db.initTestDB()
            borrow(db)
        }
    }

class TestDB {
    val denizen =
        object :
            Entity<Denizen>(
                table = "denizens",
                fields =
                    listOf(
                        "id".field("uuid"),
                        "name".field,
                        "index".field,
                        "denizen_type".field("denizen_types"),
                        "created_at".field,
                    ),
            ) {
            override fun read(resultSet: ResultSet): Denizen =
                resultSet.readRecord {
                    Denizen(
                        id = getUUID()!!,
                        name = getString(),
                        index = getInt(),
                        denizenType = getDenizenType(),
                        created = getInstant()!!,
                    )
                }

            override fun write(
                connection: Connection,
                record: Denizen,
            ): List<Param> =
                record.run {
                    listOf(id.paramAny, name.paramString, index.paramInt, denizenType.param, created.paramInstant)
                }
        }

    @Test
    fun testDenizen() =
        withTestDB { db ->
            db.withTransaction { cx ->
                val knight1 =
                    Denizen(
                        id = UUID.randomUUID(),
                        name = "knight-1",
                        index = 1,
                        denizenType = DenizenType.Knight,
                        created = Clock.System.now().dropNanos(),
                    )
                val knave1 =
                    Denizen(
                        id = UUID.randomUUID(),
                        name = "knave-1",
                        index = 2,
                        denizenType = DenizenType.Knave,
                        created = Clock.System.now().dropNanos(),
                    )
                denizen.insert(cx, listOf(knight1, knave1)).requireInserts(2)
                denizen.select(cx, "d", "WHERE d.id = ?", knight1.id.paramAny).optional!!.also {
                    it shouldBe knight1
                }
                denizen.select(cx, "d", "WHERE d.id = ?", UUID.randomUUID().paramAny).optional.shouldBeNull()
                val newName = "NEW NAME"
                val index = 1
                denizen.update(cx, listOf("name"), "index = ?", newName.paramString, index.paramInt).requireUpdates(1)
                denizen.select(cx, "d", "WHERE d.index = ?", index.paramInt).optional!!.also {
                    it shouldBe knight1.copy(name = newName)
                }
                denizen.select(cx, "d", "WHERE ?", true.paramBoolean).apply { size shouldBe 2 }
            }
        }
}
