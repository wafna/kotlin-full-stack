package wafna.dbexplorer.server

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import java.io.File

data class DatabaseConfig(val jdbcUrl: String, val username: String, val password: String, val maximumPoolSize: Int)
data class ServerConfig(val host: String, val port: Int, val static: String)
data class AppConfig(val env: String, val database: DatabaseConfig, val server: ServerConfig)

fun appConfig(configFile: File) = ConfigLoaderBuilder.default()
    .addFileSource(configFile)
    .addPropertySource(
        EnvironmentVariablesPropertySource(
            useUnderscoresAsSeparator = false,
            allowUppercaseNames = false
        )
    )
    .build()
    .loadConfigOrThrow<AppConfig>()

