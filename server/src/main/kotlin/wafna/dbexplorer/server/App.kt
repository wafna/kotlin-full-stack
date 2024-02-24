package wafna.dbexplorer.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.coroutines.runBlocking
import wafna.dbexplorer.util.LazyLogger
import java.io.File

private val log = LazyLogger(App::class)

internal class App : CliktCommand() {
    private val config: File by option(envvar = "CONFIG_FILE")
        .file(mustExist = true)
        .help("The config file to use.")
        .required()

    override fun run() = runApp(config)
}

internal fun runApp(configFile: File): Unit = runBlocking {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            log.warn { "Shutting down." }
        }
    })

    val appConfig = appConfig(configFile)

    runDB(appConfig.database) { appDB ->
        with(ServerContext(appDB)) {
            runServer(appConfig.server)
        }
    }
}

fun main(args: Array<String>): Unit = App().main(args)
