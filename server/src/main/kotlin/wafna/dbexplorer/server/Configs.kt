package wafna.dbexplorer.server

data class DatabaseConfig(val jdbcUrl: String, val username: String, val password: String, val maximumPoolSize: Int)
data class ServerConfig(val host: String, val port: Int, val static: String)
data class AppConfig(val env: String, val database: DatabaseConfig, val server: ServerConfig)
