package wafna.dbexplorer.db

import com.google.common.base.CaseFormat
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.util.UUID
import wafna.dbexplorer.test.withTestDataSource
import wafna.kdbc.Database
import wafna.kdbc.FieldNameConverter
import wafna.kdbc.insert
import wafna.kdbc.projection
import wafna.kdbc.select
import wafna.kdbc.update

data class Server(val id: UUID, val hostName: String) {
    companion object {
        val projection = projection<Server>("widgets.servers", object : FieldNameConverter {
            override fun toColumnName(name: String): String =
                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
        })
    }
}

fun Int.unique() {
    if (1 != this) fail("No value inserted.")
}

fun <T> List<T>.distinct(): T? =
    if (size > 2) fail("Too many values: $size")
    else firstOrNull()

fun List<Int>.assertUpdates(count: Int) {
    size shouldBe count
}

class TestDB(val db: Database) {
    suspend fun listServers(): List<Server> = db.withConnection {
        select(Server.projection, "ss", "")
    }

    suspend fun insertServer(server: Server): Unit = db.withConnection {
        insert(Server.projection, listOf(server)).assertUpdates(1)
    }

    suspend fun updateHost(id: UUID, hostName: String): Unit = db.withConnection {
        update("UPDATE ${Server.projection.tableName} SET host_name = ? WHERE id = ?", hostName, id).unique()
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
            db.listServers().shouldBeEmpty()
            val server = Server(UUID.randomUUID(), "example.com")
            db.insertServer(server)
            db.listServers().let { servers ->
                servers.size shouldBe 1
                servers.first().apply {
                    id shouldBe server.id
                    hostName shouldBe server.hostName
                }
            }
            val newHostName = "127.0.0.1"
            db.updateHost(server.id, newHostName)
            db.listServers().let { servers ->
                servers.size shouldBe 1
                servers.first().apply {
                    id shouldBe server.id
                    hostName shouldBe newHostName
                }
            }
        }
    }
})
