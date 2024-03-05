package wafna.fullstack.test

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun withTestH2DataSource(borrow: (DataSource) -> Unit) {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_UPPER=false;"
        username = "sa"
        password = ""
        driverClassName = org.h2.Driver::class.java.canonicalName
    }
    HikariDataSource(config).use(borrow)
}
