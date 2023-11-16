@file:Suppress("import-ordering")

package wafna.dbexplorer.db

import wafna.dbexplorer.db.marshallers.columnMarshaller
import wafna.dbexplorer.db.marshallers.constraintMarshaller
import wafna.dbexplorer.db.marshallers.indexMarshaller
import wafna.dbexplorer.db.marshallers.schemaMarshaller
import wafna.dbexplorer.db.marshallers.tableMarshaller
import wafna.dbexplorer.db.marshallers.viewMarshaller
import wafna.dbexplorer.domain.Column
import wafna.dbexplorer.domain.Constraint
import wafna.dbexplorer.domain.Index
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.domain.Table
import wafna.dbexplorer.domain.View

context (Database)
internal fun createMetaDAO(): MetaDAO = object : MetaDAO {
    override suspend fun listSchemas(): List<Schema> = select(
        """SELECT ${schemaMarshaller()}
          |FROM information_schema.schemata""".trimMargin()
    ) { schemaMarshaller(it) }

    override suspend fun listTables(schemaName: String): List<Table> = select(
        """SELECT ${tableMarshaller()}
          |FROM information_schema.tables
          |WHERE table_schema = ?""".trimMargin(),
        schemaName
    ) { tableMarshaller(it) }

    override suspend fun getTable(schemaName: String, tableName: String): Table? = select(
        """SELECT ${tableMarshaller()}
            |FROM information_schema.tables
            |WHERE table_schema = ?
            |  AND table_name = ?""".trimMargin(),
        schemaName, tableName
    ) { tableMarshaller(it) }.firstOrNull()

    override suspend fun listViews(schemaName: String): List<View> = select(
        """SELECT ${viewMarshaller()}
          |FROM information_schema.views
          |WHERE table_schema = ?""".trimMargin(),
        schemaName
    ) { viewMarshaller(it) }

    override suspend fun listColumns(schemaName: String, tableName: String): List<Column> {
        val select = select(
            """SELECT ${columnMarshaller()}
              |FROM information_schema.columns
              |WHERE table_schema = ? AND table_name = ?""".trimMargin(),
            schemaName, tableName
        ) { columnMarshaller(it) }
        return select
    }

    override suspend fun listConstraints(schemaName: String, tableName: String): List<Constraint> = select(
        """SELECT ${constraintMarshaller()}
          |FROM information_schema.table_constraints
          |WHERE table_schema = ? AND table_name = ?""".trimMargin(),
        schemaName, tableName
    ) { constraintMarshaller(it) }

    override suspend fun listIndexes(schemaName: String, tableName: String): List<Index> = select(
        """SELECT ${indexMarshaller()}
          |FROM pg_indexes
          |WHERE schemaname = ? AND tablename = ?""".trimMargin(),
        schemaName, tableName
    ) { indexMarshaller(it) }
}
