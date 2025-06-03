package wafna.fullstack.demo

import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import wafna.fullstack.api.api
import wafna.fullstack.domain.UserWip
import wafna.fullstack.util.LazyLogger

private object TestData

private val log = LazyLogger<TestData>()

// make -C database clean run
fun main(args: Array<String>) = runBlocking(Dispatchers.IO) {
    runDemo(args) { db ->
        // We can instrument the DB directly.
        val users = db.transact { cx ->
            db.users.insert(cx, UserWip("herp"), UserWip("derp"))
        }.getOrThrow()
        // Or wrap it in the API and play nice.
        val api = api(db)
        coroutineScope {
            users.forEach { user ->
                launch {
                    require(api.users.byId(user.id).getOrThrow()!! == user) { "biffed user $user" }
                    for (nthBlock in 0 until 2) {
                        val records = (0 until 10).map { i ->
                            "record-$i" to List(100) { Random().nextGaussian(50.0, 10.0).toString() }
                        }
                        val dataBlock = api.dataBlocks.create(user.id, "block-$nthBlock", records).getOrThrow()
                        log.info { dataBlock.toString() }
                    }
                }
            }
        }
    }
}

