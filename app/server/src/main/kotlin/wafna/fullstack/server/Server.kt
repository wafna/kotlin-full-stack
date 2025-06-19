package wafna.fullstack.server

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.CacheControl
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.getAllRoutes
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.util.hex
import java.io.File
import wafna.fullstack.api.API
import wafna.fullstack.kdbc.AppDb
import wafna.fullstack.kdbc.appDb
import wafna.fullstack.server.routes.sessionRoutes
import wafna.fullstack.server.routes.dataRoutes
import wafna.fullstack.util.LazyLogger

private object Server

private val logger = LazyLogger(Server::class)

fun DatabaseConfig.hikariConfig() = HikariConfig().also {
    it.jdbcUrl = jdbcUrl
    it.username = username
    it.password = password
    it.maximumPoolSize = maximumPoolSize
}

internal suspend fun runDB(config: DatabaseConfig, borrow: suspend (AppDb) -> Unit) {
    logger.info { "Connection to database ${config.jdbcUrl}" }
    HikariDataSource(config.hikariConfig()).use { dataSource ->
        val appDB = appDb(dataSource)
        borrow(appDB)
    }
}

internal fun runServer(api: API, config: ServerConfig) {
    embeddedServer(
        factory = Netty,
        environment = applicationEnvironment { log = logger.log },
        configure = {
            connector {
                port = config.port
                host = config.host
            }
        },
    ) {
        installSessions(config.session)
        installCORS()
        installRoutes(api, File(config.static))
        logger.info { "\u263a Starting server: ${config.host}:${config.port}" }
    }.start(wait = true)
}

private fun Application.installRoutes(api: API, staticDir: File) {
    require(staticDir.isDirectory) { "Static directory not found: ${staticDir.canonicalPath}" }
    logger.info { "Serving static directory: ${staticDir.canonicalPath}" }
    install(AccessPlugin)
    routing {
        route("/api") {
            route("/session") { sessionRoutes(api) }
            route("/data") { dataRoutes(api) }
        }
        staticFiles(remotePath = "/", dir = staticDir) {
            // Best not to cache anything since almost all the static content is JavaScript.
            cacheControl {
                listOf(CacheControl.NoCache(CacheControl.Visibility.Public))
            }
        }
    }.run {
        logger.debug { "--- All Routes:\n   ${getAllRoutes().joinToString("\n   ")}" }
    }
}

private fun Application.installSessions(sessionConfig: SessionConfig) {
    val domain = sessionConfig.domain
    val maxAgeInSeconds = sessionConfig.maxAgeInSeconds
    val signingKey = hex(sessionConfig.signingKey)
    val encryptionKey = hex(sessionConfig.encryptionKey)
    install(Sessions) {
        cookie<UserSession>(USER_SESSION) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = maxAgeInSeconds
            cookie.secure = false
            cookie.domain = domain
            transform(SessionTransportTransformerEncrypt(encryptionKey, signingKey))
        }
    }
}

private fun Application.installCORS() {
    install(CORS) {
        anyHost()
        allowOrigins { true }
        allowHeaders { true }
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowNonSimpleContentTypes = true
        methods.addAll(listOf(HttpMethod.Get, HttpMethod.Delete, HttpMethod.Post, HttpMethod.Put))
    }
}
