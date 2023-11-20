@file:Suppress("import-ordering")

package wafna.dbexplorer.db

import wafna.dbexplorer.domain.Column
import wafna.dbexplorer.domain.ForeignKey
import wafna.dbexplorer.domain.Index
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.domain.Table
import wafna.dbexplorer.domain.TableConstraint
import wafna.dbexplorer.domain.View
import wafna.dbexplorer.util.LazyLogger
import java.sql.ResultSet
import javax.sql.DataSource

fun createAppDB(dataSource: DataSource): AppDB {
    with(Database(dataSource)) {
        return object : AppDB {
            override val meta: MetaDAO = createMetaDAO()
        }
    }
}

context(Database)
suspend fun <T> LazyLogger.selectLogged(sql: String, vararg params: Any, reader: suspend (ResultSet) -> T): List<T> {
    debug { "SQL\n$sql" }
    return select(sql, *params) { reader(it) }
}

interface AppDB {
    val meta: MetaDAO
}

interface MetaDAO {
    suspend fun listSchemas(): List<Schema>
    suspend fun listTables(schemaName: String): List<Table>
    suspend fun getTable(schemaName: String, tableName: String): Table?
    suspend fun listViews(schemaName: String): List<View>
    suspend fun listColumns(schemaName: String, tableName: String): List<Column>
    suspend fun listTableConstraints(schemaName: String, tableName: String): List<TableConstraint>
    suspend fun listIndexes(schemaName: String, tableName: String): List<Index>
    suspend fun listForeignKeys(schemaName: String, tableName: String): List<ForeignKey>
    suspend fun listForeignKeyRefs(schemaName: String, tableName: String): List<ForeignKey>
}
