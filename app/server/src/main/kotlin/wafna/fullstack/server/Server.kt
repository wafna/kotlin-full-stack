package wafna.fullstack.server

import arrow.core.Either
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import wafna.fullstack.db.AppDb
import wafna.fullstack.db.appDb
import wafna.fullstack.server.controllers.apiController
import wafna.fullstack.server.routes.api
import wafna.fullstack.util.LazyLogger
import java.io.File
import java.lang.reflect.Type
import java.util.*

private object Server

private val log = LazyLogger(Server::class)

fun DatabaseConfig.hikariConfig() = HikariConfig().also {
    it.jdbcUrl = jdbcUrl
    it.username = username
    it.password = password
    it.maximumPoolSize = maximumPoolSize
}

internal suspend fun runDB(config: DatabaseConfig, borrow: suspend (AppDb) -> Unit) {
    HikariDataSource(config.hikariConfig()).use { dataSource ->
        val appDB = appDb(dataSource)
        borrow(appDB)
    }
}

data class ServerContext(val db: AppDb)

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
            route("/api") { with(apiController()) { api() } }
            route("/") { staticFiles(remotePath = "/", dir = staticDir) }
        }
    }
}

fun Application.installContentNegotiation() {
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
