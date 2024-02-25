package wafna.dbexplorer.server

import arrow.core.Either
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.HttpMethod
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import wafna.dbexplorer.db.AppDb
import wafna.dbexplorer.db.createAppDb
import wafna.dbexplorer.server.routes.api
import wafna.dbexplorer.util.LazyLogger
import java.io.File
import java.lang.reflect.Type
import java.util.UUID

private object Server

private val log = LazyLogger(Server::class)

data class ServerContext(val db: AppDb)

fun DatabaseConfig.hikariConfig() = HikariConfig().also {
    it.jdbcUrl = jdbcUrl
    it.username = username
    it.password = password
    it.maximumPoolSize = maximumPoolSize
}

internal suspend fun runDB(config: DatabaseConfig, callback: suspend (AppDb) -> Unit) {
    HikariDataSource(config.hikariConfig()).use { dataSource ->
        val appDB = createAppDb(dataSource)
        callback(appDB)
    }
}

context(ServerContext)
internal fun runServer(config: ServerConfig) {
    val environment = applicationEngineEnvironment {
        connector {
            port = config.port
            host = config.host
        }
        module {
            installCORS()
            installContentNegotiation()
            installRoutes(File(config.static))
        }
    }
    embeddedServer(Netty, environment).apply {
        log.info { "Starting server at ${config.host}:${config.port}" }
        start(wait = true)
    }
}

context(ServerContext)
private fun Application.installRoutes(staticDir: File) {
    require(staticDir.isDirectory) { "Static directory not found: ${staticDir.canonicalPath}" }
    log.info { "Serving static directory: ${staticDir.canonicalPath}" }

    routing {
        accessLog {
            route("/api") { api() }
            route("/") { staticFiles(remotePath = "/", dir = staticDir) }
        }
    }
}

private fun Application.installContentNegotiation() {
    install(ContentNegotiation) {
        gson {
            disableHtmlEscaping()
            serializeNulls()
            registerTypeAdapter(
                UUID::class.java,
                object : JsonDeserializer<UUID> {
                    override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?
                    ): UUID = Either.catch {
                        json!!.asString.let { UUID.fromString(it) }
                    }.onLeft { e ->
                        throw IllegalArgumentException("Failed to serialize UUID from ${json?.asString}", e)
                    }.getOrNull()!!
                }
            )
        }
    }
}

private fun Application.installCORS() {
    install(CORS) {
        anyHost()
        allowHeaders { true }
        allowNonSimpleContentTypes = true
        methods.addAll(listOf(HttpMethod.Get, HttpMethod.Delete, HttpMethod.Post, HttpMethod.Put))
    }
}
