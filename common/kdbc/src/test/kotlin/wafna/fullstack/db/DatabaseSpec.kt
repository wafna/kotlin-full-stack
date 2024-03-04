package wafna.fullstack.db

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

class DatabaseSpec {
    @Test
    fun testDB() {
        withH2TestDB { db ->
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
