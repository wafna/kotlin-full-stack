package wafna.fullstack.db

import com.google.common.base.CaseFormat
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.fail
import io.kotest.common.runBlocking
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import wafna.kdbc.Database
import wafna.kdbc.FieldNameConverter
import wafna.kdbc.delete
import wafna.kdbc.insert
import wafna.kdbc.projection
import wafna.kdbc.select
import wafna.kdbc.update
import java.util.*
import javax.sql.DataSource

/**
 * The sole domain object.
 */
data class Thingy(val id: UUID, val name: String) {
    companion object {
        val projection = projection<Thingy>(
            tableName = "testing.thingy",
            fieldNameConverter = object : FieldNameConverter {
                override fun toColumnName(name: String): String =
                    CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
            })
    }
}

fun <T> List<T>.optional(): T? = when {
    isEmpty() -> null
    else -> first()
}

fun Int.unique() {
    if (1 != this) fail("No value inserted.")
}

fun List<Int>.assertUpdates(count: Int) {
    size shouldBe count
}

/**
 * CRUD for thingies.
 */
class TestDB internal constructor(private val db: Database) {
    suspend fun list(): List<Thingy> = db.transact {
        select(Thingy.projection, "ss", "")
    }

    suspend fun insert(vararg thingies: Thingy): Unit = db.transact {
        insert(Thingy.projection, thingies.toList()).assertUpdates(thingies.size)
    }

    suspend fun byId(id: UUID): Thingy? = db.transact {
        select(Thingy.projection, "ss", "WHERE id = ?", id).optional()
    }

    suspend fun update(id: UUID, name: String): Unit = db.transact {
        update("UPDATE ${Thingy.projection.tableName} SET name = ? WHERE id = ?", name, id).unique()
    }

    suspend fun delete(id: UUID): Unit = db.transact {
        delete(Thingy.projection, "id = ?", id).unique()
    }
}

suspend fun withTestH2DataSource(borrow: suspend (DataSource) -> Unit) {
    HikariConfig()
        .apply {
            jdbcUrl = "jdbc:h2:mem:"
            username = "sa"
            password = ""
            driverClassName = org.h2.Driver::class.java.canonicalName
        }.let {
            HikariDataSource(it)
        }.use {
            borrow(it)
        }
}

suspend fun withTestDB(borrow: suspend (TestDB) -> Unit) {
    withTestH2DataSource { dataSource ->
        val db = Database(dataSource)
        db.autoCommit {
            update("CREATE SCHEMA testing")
            update("CREATE TABLE testing.thingy(id UUID, name TEXT)")
        }
        borrow(TestDB(db))
    }
}

class DatabaseSpec  {
    @Test
    fun testDB() = runBlocking {
        withTestDB { db ->
            db.list().shouldBeEmpty()
            val thingy = Thingy(UUID.randomUUID(), "Smith")
            db.insert(thingy)
            db.list().let { servers ->
                servers.size shouldBe 1
                servers.first().apply {
                    id shouldBe thingy.id
                    name shouldBe thingy.name
                }
            }
            val newName = "Jones"
            db.update(thingy.id, newName)
            db.list().let { servers ->
                servers.size shouldBe 1
                servers.first().apply {
                    id shouldBe thingy.id
                    name shouldBe newName
                }
            }
            db.byId(thingy.id)!!.apply {
                id shouldBe thingy.id
                name shouldBe newName
            }
            db.byId(UUID.randomUUID()).shouldBeNull()
            db.delete(thingy.id)
            db.list().shouldBeEmpty()
            val thingies = listOf("Bob", "Carol", "Ted", "Alice").map { Thingy(UUID.randomUUID(), it) }
            db.insert(* thingies.toTypedArray())
            db.list().size shouldBe thingies.size
        }
    }
}
