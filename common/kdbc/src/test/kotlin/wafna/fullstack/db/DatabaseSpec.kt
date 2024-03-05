package wafna.fullstack.db

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

class DatabaseSpec {
    @Test
    fun testDB() {
        withH2TestDB { db ->
            db.selectAll().shouldBeEmpty()
            val thingy = Thingy(UUID.randomUUID(), "Smith")
            withClue("insert one") {
                db.insert(thingy)
                db.selectAll().let { servers ->
                    servers.size shouldBe 1
                    servers.first().apply {
                        id shouldBe thingy.id
                        name shouldBe thingy.name
                    }
                }
            }
            withClue("update") {
                val newName = "Jones"
                db.update(thingy.id, newName)
                db.selectAll().apply {
                    size shouldBe 1
                    first().apply {
                        id shouldBe thingy.id
                        name shouldBe newName
                    }
                }
                db.selectById(thingy.id)!!.apply {
                    id shouldBe thingy.id
                    name shouldBe newName
                }
            }
            withClue("null") {
                db.update(thingy.id, null)
                db.selectById(thingy.id)!!.apply {
                    id shouldBe thingy.id
                    name.shouldBeNull()
                }
            }
            withClue("not found") {
                db.selectById(UUID.randomUUID()).shouldBeNull()
            }
            withClue("delete") {
                db.delete(thingy.id)
                db.selectAll().shouldBeEmpty()
            }
            withClue("insert many") {
                val thingies = listOf("Bob", "Carol", "Ted", "Alice").map { Thingy(UUID.randomUUID(), it) }
                db.insert(* thingies.toTypedArray())
                db.selectAll().size shouldBe thingies.size
            }
        }
    }
}
