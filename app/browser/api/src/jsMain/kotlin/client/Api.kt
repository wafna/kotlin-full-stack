package client

open class ServerApi(apiRoot: String) {
    val session = sessionApi(apiRoot, "session")
    val data = dataApi(apiRoot, "data")
}

val Api = ServerApi("http://localhost:8686/api")

