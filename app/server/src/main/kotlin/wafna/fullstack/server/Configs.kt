package wafna.fullstack.server

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addFileSource
import java.io.File

data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int
)

data class SessionConfig(
    val domain: String,
    val maxAgeInSeconds: Long,
    val signingKey: String,
    val encryptionKey: String
)

data class ServerConfig(
    val host: String,
    val port: Int,
    val session: SessionConfig,
    val static: String
)

data class AppConfig(
    val appEnv: String,
    val database: DatabaseConfig,
    val server: ServerConfig
)

@OptIn(ExperimentalHoplite::class)
fun appConfig(configFile: File) =
    ConfigLoaderBuilder.default()
        .addFileSource(configFile)
        .withExplicitSealedTypes()
        .build()
        .loadConfigOrThrow<AppConfig>()
