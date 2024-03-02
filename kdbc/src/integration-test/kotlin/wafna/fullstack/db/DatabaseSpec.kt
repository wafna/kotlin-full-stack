package wafna.fullstack.db

import com.google.common.base.CaseFormat
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import wafna.fullstack.test.withTestDataSource
import wafna.kdbc.Database
import wafna.kdbc.FieldNameConverter
import wafna.kdbc.delete
import wafna.kdbc.insert
import wafna.kdbc.projection
import wafna.kdbc.select
import wafna.kdbc.update
import java.util.*

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

suspend fun withTestDB(borrow: suspend (TestDB) -> Unit) {
    withTestDataSource { dataSource ->
        borrow(TestDB(Database(dataSource)))
    }
}

class DatabaseSpec : StringSpec({
    "select, insert, update" {
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
})
