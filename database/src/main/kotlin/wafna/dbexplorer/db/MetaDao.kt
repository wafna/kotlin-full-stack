package wafna.dbexplorer.db

import wafna.dbexplorer.db.Projections.columnMarshaller
import wafna.dbexplorer.db.Projections.foreignKeyMarshaller
import wafna.dbexplorer.db.Projections.indexMarshaller
import wafna.dbexplorer.db.Projections.schemaMarshaller
import wafna.dbexplorer.db.Projections.tableConstraintMarshaller
import wafna.dbexplorer.db.Projections.tableMarshaller
import wafna.dbexplorer.db.Projections.viewMarshaller
import wafna.dbexplorer.domain.Column
import wafna.dbexplorer.domain.ForeignKey
import wafna.dbexplorer.domain.Index
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.domain.Table
import wafna.dbexplorer.domain.TableConstraint
import wafna.dbexplorer.domain.View
import wafna.dbexplorer.domain.errors.DomainResult
import wafna.dbexplorer.util.LazyLogger
import wafna.kdbc.Database
import wafna.kdbc.select

private val log = LazyLogger(MetaDao::class)

interface MetaDao {
    suspend fun listSchemas(): DomainResult<List<Schema>>
    suspend fun listTables(schemaName: String): DomainResult<List<Table>>
    suspend fun getTable(schemaName: String, tableName: String): DomainResult<Table?>
    suspend fun listViews(schemaName: String): DomainResult<List<View>>
    suspend fun listColumns(schemaName: String, tableName: String): DomainResult<List<Column>>
    suspend fun listTableConstraints(schemaName: String, tableName: String): DomainResult<List<TableConstraint>>
    suspend fun listIndexes(schemaName: String, tableName: String): DomainResult<List<Index>>
    suspend fun listForeignKeys(schemaName: String, tableName: String): DomainResult<List<ForeignKey>>
    suspend fun listForeignKeyRefs(schemaName: String, tableName: String): DomainResult<List<ForeignKey>>
}

context (Database)
internal fun metaDAO() = object : MetaDao {
    override suspend fun listSchemas(): DomainResult<List<Schema>> = domainResult {
        withConnection {
            select(
                schemaMarshaller,
                """SELECT ${schemaMarshaller.alias("ss")}
            |FROM information_schema.schemata ss
            """.trimMargin()
            )
        }
    }

    override suspend fun listTables(
        schemaName: String
    ): DomainResult<List<Table>> = domainResult {
        withConnection {
            select(
                tableMarshaller,
                """SELECT ${tableMarshaller.alias("ts")}
            |FROM information_schema.tables ts
            |WHERE ts.table_schema = ?
            """.trimMargin(),
                schemaName
            )
        }
    }

    override suspend fun getTable(
        schemaName: String,
        tableName: String
    ): DomainResult<Table?> = domainResult {
        withConnection {
            select(
                tableMarshaller,
                """SELECT ${tableMarshaller.alias("ts")}
            |FROM information_schema.tables ts
            |WHERE ts.table_schema = ?
            |  AND ts.table_name = ?
            """.trimMargin(),
                schemaName,
                tableName
            ).firstOrNull()
        }
    }

    override suspend fun listViews(
        schemaName: String
    ): DomainResult<List<View>> = domainResult {
        withConnection {
            select(
                viewMarshaller,
                """SELECT ${viewMarshaller.alias("vs")}
            |FROM information_schema.views vs
            |WHERE vs.table_schema = ?
            """.trimMargin(),
                schemaName
            )
        }
    }

    override suspend fun listColumns(
        schemaName: String,
        tableName: String
    ): DomainResult<List<Column>> = domainResult {
        withConnection {
            select(
                columnMarshaller,
                """SELECT ${columnMarshaller.alias("cs")}
            |FROM information_schema.columns cs
            |WHERE cs.table_schema = ? AND cs.table_name = ?
            """.trimMargin(),
                schemaName,
                tableName
            )
        }
    }

    override suspend fun listTableConstraints(
        schemaName: String,
        tableName: String
    ): DomainResult<List<TableConstraint>> = domainResult {
        withConnection {
            select(
                tableConstraintMarshaller,
                """SELECT ${tableConstraintMarshaller.alias("tcs")}
            |FROM information_schema.table_constraints tcs
            |WHERE tcs.table_schema = ? AND tcs.table_name = ?
            """.trimMargin(),
                schemaName,
                tableName
            )
        }
    }

    override suspend fun listIndexes(
        schemaName: String,
        tableName: String
    ): DomainResult<List<Index>> = domainResult {
        withConnection {
            select(
                indexMarshaller,
                """SELECT ${indexMarshaller.alias("ixs")}
            |FROM pg_indexes ixs
            |WHERE ixs.schemaname = ? AND ixs.tablename = ?
            """.trimMargin(),
                schemaName,
                tableName
            )
        }
    }

    override suspend fun listForeignKeys(
        schemaName: String,
        tableName: String
    ): DomainResult<List<ForeignKey>> = domainResult {
        withConnection {
            select(
                foreignKeyMarshaller,
                foreignKeys(FKDirection.FROM),
                schemaName,
                tableName
            )
        }
    }

    override suspend fun listForeignKeyRefs(
        schemaName: String,
        tableName: String
    ): DomainResult<List<ForeignKey>> = domainResult {
        withConnection {
            select(
                foreignKeyMarshaller,
                foreignKeys(FKDirection.TO),
                schemaName,
                tableName
            )
        }
    }
}

private enum class FKDirection(val tableName: String) {
    FROM("tc"),
    TO("ccu")
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
