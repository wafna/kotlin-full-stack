package wafna.fullstack.demo

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addFileSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.File
import kotlinx.coroutines.runBlocking
import wafna.fullstack.kdbc.AppDb
import wafna.fullstack.kdbc.appDb
import wafna.fullstack.util.LazyLogger

private object TestData1

private val log = LazyLogger<TestData1>()

fun runDemo(args: Array<String>, runDb: suspend (AppDb) -> Unit) {
    try {
        object : CliktCommand() {
            private val configFile: File by option(envvar = "CONFIG")
                .file(mustExist = true)
                .help("The config file to use.")
                .required()

            override fun run() = runBlocking {
                log.info { "Loading config ${configFile.absolutePath}" }
                val appConfig = appConfig(configFile)
                val databaseConfig = appConfig.database
                log.info { "Connecting to database ${databaseConfig.jdbcUrl}" }
                HikariDataSource(databaseConfig.hikariConfig()).use { dataSource ->
                    val appDB = appDb(dataSource)
                    runDb(appDB)
                }
            }
        }.main(args)
    } catch (e: Throwable) {
        log.error(e) { "FAILURE" }
    }
}

fun DatabaseConfig.hikariConfig() = HikariConfig().also {
    it.jdbcUrl = jdbcUrl
    it.username = username
    it.password = password
    it.maximumPoolSize = maximumPoolSize
}

data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int
)

data class AppConfig(
    val database: DatabaseConfig,
)

@OptIn(ExperimentalHoplite::class)
fun appConfig(configFile: File) =
    ConfigLoaderBuilder.default()
        .addFileSource(configFile)
        .withExplicitSealedTypes()
        .build()
        .loadConfigOrThrow<AppConfig>()
