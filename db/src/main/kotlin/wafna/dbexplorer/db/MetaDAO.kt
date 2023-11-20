package wafna.dbexplorer.db

import wafna.dbexplorer.db.marshallers.columnMarshaller
import wafna.dbexplorer.db.marshallers.foreignKeyMarshaller
import wafna.dbexplorer.db.marshallers.indexMarshaller
import wafna.dbexplorer.db.marshallers.schemaMarshaller
import wafna.dbexplorer.db.marshallers.tableConstraintMarshaller
import wafna.dbexplorer.db.marshallers.tableMarshaller
import wafna.dbexplorer.db.marshallers.viewMarshaller
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.util.LazyLogger
import java.sql.ResultSet

context (Database)
internal fun createMetaDAO() = object : MetaDAO {
    val log = LazyLogger(this::class)
    suspend fun <T> selectLogged(sql: String, vararg params: Any, reader: suspend (ResultSet) -> T): List<T> {
        log.debug { "SQL\n$sql" }
        return select(sql, *params) { reader(it) }
    }

    override suspend fun listSchemas(): List<Schema> = selectLogged(
        """SELECT ${schemaMarshaller.project("ss")}
        |FROM information_schema.schemata ss
        """.trimMargin()
    ) { schemaMarshaller.read(it) }

    override suspend fun listTables(schemaName: String) = selectLogged(
        """SELECT ${tableMarshaller.project("ts")}
        |FROM information_schema.tables ts
        |WHERE ts.table_schema = ?
        """.trimMargin(),
        schemaName
    ) { tableMarshaller.read(it) }

    override suspend fun getTable(schemaName: String, tableName: String) = selectLogged(
        """SELECT ${tableMarshaller.project("ts")}
        |FROM information_schema.tables ts
        |WHERE ts.table_schema = ?
        |  AND ts.table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { tableMarshaller.read(it) }.firstOrNull()

    override suspend fun listViews(schemaName: String) = selectLogged(
        """SELECT ${viewMarshaller.project("vs")}
        |FROM information_schema.views vs
        |WHERE vs.table_schema = ?
        """.trimMargin(),
        schemaName
    ) { viewMarshaller.read(it) }

    override suspend fun listColumns(schemaName: String, tableName: String) = selectLogged(
        """SELECT ${columnMarshaller.project("cs")}
        |FROM information_schema.columns cs
        |WHERE cs.table_schema = ? AND cs.table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { columnMarshaller.read(it) }

    override suspend fun listTableConstraints(schemaName: String, tableName: String) = selectLogged(
        """SELECT ${tableConstraintMarshaller.project()}
        |FROM information_schema.table_constraints
        |WHERE table_schema = ? AND table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { tableConstraintMarshaller.read(it) }

    override suspend fun listIndexes(schemaName: String, tableName: String) = selectLogged(
        """SELECT ${indexMarshaller.project()}
        |FROM pg_indexes
        |WHERE schemaname = ? AND tablename = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { indexMarshaller.read(it) }

    override suspend fun listForeignKeys(schemaName: String, tableName: String) = selectLogged(
        foreignKeys(FKDirection.FROM),
        schemaName,
        tableName
    ) { foreignKeyMarshaller.read(it) }

    override suspend fun listForeignKeyRefs(schemaName: String, tableName: String) = selectLogged(
        foreignKeys(FKDirection.TO),
        schemaName,
        tableName
    ) { foreignKeyMarshaller.read(it) }
}

private enum class FKDirection(val tableName: String) {
    FROM("tc"), TO("ccu")
}

private fun foreignKeys(dir: FKDirection) =
    """SELECT
        |    tc.table_schema, 
        |    tc.table_name, 
        |    tc.constraint_name, 
        |    kcu.column_name, 
        |    ccu.table_schema AS foreign_table_schema,
        |    ccu.table_name AS foreign_table_name,
        |    ccu.column_name AS foreign_column_name 
        |FROM information_schema.table_constraints AS tc 
        |JOIN information_schema.key_column_usage AS kcu
        |    ON tc.constraint_name = kcu.constraint_name
        |    AND tc.table_schema = kcu.table_schema
        |JOIN information_schema.constraint_column_usage AS ccu
        |    ON ccu.constraint_name = tc.constraint_name
        |WHERE tc.constraint_type = 'FOREIGN KEY'
        |    AND ${dir.tableName}.table_schema = ?
        |    AND ${dir.tableName}.table_name = ?
        """.trimMargin()

