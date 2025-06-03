package wafna.fullstack.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.coroutines.runBlocking
import wafna.fullstack.api.api
import wafna.fullstack.util.LazyLogger
import java.io.File

private val log = LazyLogger(App::class)

private fun runApp(configFile: File): Unit = runBlocking {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            log.warn { "Shutting down." }
        }
    })

    log.info { "Loading config ${configFile.absolutePath}" }
    val appConfig = appConfig(configFile)

    runDB(appConfig.database) { db ->
        runServer(api(db), appConfig.server)
    }
}

private object App : CliktCommand() {
    private val config: File by option(envvar = "CONFIG")
        .file(mustExist = true)
        .help("The config file to use.")
        .required()

    override fun run() = runApp(config)
}

fun main(args: Array<String>) =
    try {
        App.main(args)
    } catch (e: Throwable) {
        log.error(e) { "FAILURE" }
    }
