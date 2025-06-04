package wafna.fullstack.api

import wafna.fullstack.kdbc.AppDb

interface API {
    val users: UsersAPI
    val dataBlocks: DataBlocksAPI
}

fun api(db: AppDb): API =
    object : API {
        override val users = usersAPI(db)
        override val dataBlocks = dataBlocksAPI(db)
    }
