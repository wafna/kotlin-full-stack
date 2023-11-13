package wafna.dbexplorer.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.types.file
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import java.io.File
import kotlinx.coroutines.runBlocking
import wafna.dbexplorer.util.LazyLogger

private val log = LazyLogger(App::class)

class App : CliktCommand() {
    private val config: File by argument().file(mustExist = true).help("The config file to use.")
    override fun run() = runApp(config)
}

fun runApp(configFile: File): Unit = runBlocking {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            log.warn { "Shutting down." }
        }
    })

    val appConfig = ConfigLoaderBuilder.default()
        .addFileSource(configFile)
        .addPropertySource(EnvironmentVariablesPropertySource(false, false))
        .build()
        .loadConfigOrThrow<AppConfig>()

    runDB(appConfig.database) { appDB ->
        with(ServerContext(appDB)) {
            runServer(appConfig.server)
        }
    }
}

fun main(args: Array<String>): Unit = App().main(args)
