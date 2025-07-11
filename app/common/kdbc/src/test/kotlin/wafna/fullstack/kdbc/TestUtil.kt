package wafna.fullstack.kdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.fail
import javax.sql.DataSource
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/** Spins up a database container, migrates it, and loans a data source. */
fun withPGDB(borrow: suspend (DataSource) -> Unit) {
    PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
        .withDatabaseName("test")
        .withUsername("username")
        .withPassword("password")
        ?.apply {
            start()
            use { container ->
                val config =
                    HikariConfig().apply {
                        jdbcUrl = container.jdbcUrl
                        username = container.username
                        password = container.password
                        driverClassName = container.driverClassName
                        maximumPoolSize = 32
                    }
                runBlocking { borrow(HikariDataSource(config)) }
            }
        } ?: fail("Failed to create container.")
}
