package wafna.fullstack.test

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.runBlocking
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

/**
 * Spins up a database container, migrates it, and loans a data source.
 */
fun withTestDataSource(borrow: suspend (DataSource) -> Unit) {
    PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
        .withDatabaseName("integration-test")
        .withUsername("username")
        .withPassword("password")
        .apply { start() }
        .use { container ->
            val dataSource = HikariConfig()
                .apply {
                    jdbcUrl = container.jdbcUrl
                    username = container.username
                    password = container.password
                    driverClassName = container.driverClassName
                }.let {
                    HikariDataSource(it)
                }
            Flyway(
                FluentConfiguration()
                    .dataSource(dataSource)
                    .locations("classpath:db/migrations")
            ).migrate()
            runBlocking { borrow(dataSource) }
        }
}
