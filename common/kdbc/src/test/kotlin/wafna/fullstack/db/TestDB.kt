package wafna.fullstack.db

import com.google.common.base.CaseFormat
import io.kotest.assertions.fail
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import wafna.fullstack.test.withTestH2DataSource
import wafna.kdbc.FieldNameConverter
import wafna.kdbc.transact
import wafna.kdbc.insertRecords
import wafna.kdbc.projection
import wafna.kdbc.selectRecords
import wafna.kdbc.transact
import wafna.kdbc.updateRecords
import java.util.*
import javax.sql.DataSource

/**
 * The sole domain object.
 */
data class Thingy(val id: UUID, val name: String?) {
    companion object {
        val projection = projection<Thingy>(
            tableName = "testing.thingy",
            fieldNameConverter = camelToSnake
        )
    }
}

val camelToSnake = object : FieldNameConverter {
    override fun toColumnName(name: String): String =
        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
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
 * Mixes transact and autoCommit to test both.
 */
class TestDB internal constructor(private val db: DataSource) {
    private val thingies = Thingy.projection
    private val selector = thingies.selectSql("ts")
    suspend fun selectAll(): List<Thingy> = db.transact {
        selectRecords<Thingy>(selector)().read(thingies)
    }

    suspend fun selectById(id: UUID): Thingy? = db.transact {
        selectRecords<Thingy>("$selector WHERE id = ?")(id).read(thingies).optional()
    }

    suspend fun insert(vararg thingies: Thingy): Unit = db.transact {
        insertRecords(this@TestDB.thingies.tableName, this@TestDB.thingies.columnNames, thingies.toList())
            .write(this@TestDB.thingies).assertUpdates(thingies.size)
    }

    suspend fun update(id: UUID, name: String?): Unit = db.transact {
        updateRecords("UPDATE ${thingies.tableName} SET name = ? WHERE id = ?")(name, id).unique()
    }

    suspend fun delete(id: UUID): Unit = db.transact {
        updateRecords("${thingies.deleteSql()} id = ?")(id).unique()
    }
}

/**
 * Migrates the data source and wraps the TestDB API around it.
 */
fun testDB(dataSource: DataSource): TestDB {
    Flyway(
        FluentConfiguration()
            .dataSource(dataSource)
            .locations("classpath:db/migrations/kdbc")
    ).migrate()
    return TestDB(dataSource)
}

/**
 * Create a TestDB on H2 in memory.
 */
fun withH2TestDB(borrow: suspend (TestDB) -> Unit) {
    withTestH2DataSource { dataSource ->
        runBlocking {
            borrow(testDB(dataSource))
        }
    }
}
