package wafna.dbexplorer.db

import com.google.common.base.CaseFormat
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.util.UUID
import test.withTestDataSource
import wafna.kdbc.Database
import wafna.kdbc.FieldNameConverter
import wafna.kdbc.genericMarshaller

data class Server(val id: UUID, val hostName: String) {
    companion object {
        val marshaller = genericMarshaller<Server>(object : FieldNameConverter {
            override fun toColumnName(name: String): String =
                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
        })
    }
}

fun Int.unique() {
    if (1 != this) fail("No value inserted.")
}

class TestDB(val db: Database) {
    suspend fun listServers(): List<Server> =
        db.select(Server.marshaller, "SELECT ${Server.marshaller.project()} FROM widgets.servers")

    suspend fun insertServer(server: Server): Unit =
        db.update(
            "INSERT INTO widgets.servers (${Server.marshaller.project()}) VALUES ${Server.marshaller.inList()}",
            server.id,
            server.hostName
        ).unique()
}

suspend fun withTestDB(borrow: suspend (TestDB) -> Unit) {
    withTestDataSource { dataSource ->
        borrow(TestDB(Database(dataSource)))
    }
}

class DatabaseSpec : StringSpec({
    "select and insert" {
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
        }
    }
})
