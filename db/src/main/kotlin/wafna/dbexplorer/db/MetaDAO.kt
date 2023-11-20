@file:Suppress("import-ordering")

package wafna.dbexplorer.db

import wafna.dbexplorer.db.marshallers.columnMarshaller
import wafna.dbexplorer.db.marshallers.foreignKeyMarshaller
import wafna.dbexplorer.db.marshallers.indexMarshaller
import wafna.dbexplorer.db.marshallers.schemaMarshaller
import wafna.dbexplorer.db.marshallers.tableConstraintMarshaller
import wafna.dbexplorer.db.marshallers.tableMarshaller
import wafna.dbexplorer.db.marshallers.viewMarshaller
import wafna.dbexplorer.domain.Schema

context (Database)
internal fun createMetaDAO() = object : MetaDAO {
    override suspend fun listSchemas(): List<Schema> = select(
        """SELECT ${schemaMarshaller()}
        |FROM information_schema.schemata
        """.trimMargin()
    ) { schemaMarshaller(it) }

    override suspend fun listTables(schemaName: String) = select(
        """SELECT ${tableMarshaller()}
        |FROM information_schema.tables
        |WHERE table_schema = ?
        """.trimMargin(),
        schemaName
    ) { tableMarshaller(it) }

    override suspend fun getTable(schemaName: String, tableName: String) = select(
        """SELECT ${tableMarshaller()}
        |FROM information_schema.tables
        |WHERE table_schema = ?
        |  AND table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { tableMarshaller(it) }.firstOrNull()

    override suspend fun listViews(schemaName: String) = select(
        """SELECT ${viewMarshaller()}
        |FROM information_schema.views
        |WHERE table_schema = ?
        """.trimMargin(),
        schemaName
    ) { viewMarshaller(it) }

    override suspend fun listColumns(schemaName: String, tableName: String) = select(
        """SELECT ${columnMarshaller()}
            |FROM information_schema.columns
            |WHERE table_schema = ? AND table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { columnMarshaller(it) }

    override suspend fun listTableConstraints(schemaName: String, tableName: String) = select(
        """SELECT ${tableConstraintMarshaller()}
        |FROM information_schema.table_constraints
        |WHERE table_schema = ? AND table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { tableConstraintMarshaller(it) }

    override suspend fun listIndexes(schemaName: String, tableName: String) = select(
        """SELECT ${indexMarshaller()}
        |FROM pg_indexes
        |WHERE schemaname = ? AND tablename = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { indexMarshaller(it) }

    override suspend fun listForeignKeys(schemaName: String, tableName: String) = select(
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
        |    AND tc.table_schema = ?
        |    AND tc.table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { foreignKeyMarshaller(it) }

    override suspend fun listForeignKeyRefs(schemaName: String, tableName: String) = select(
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
        |    AND ccu.table_schema = ?
        |    AND ccu.table_name = ?
        """.trimMargin(),
        schemaName,
        tableName
    ) { foreignKeyMarshaller(it) }
}
