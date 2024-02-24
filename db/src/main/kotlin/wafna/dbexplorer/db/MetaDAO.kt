package wafna.dbexplorer.db

import wafna.dbexplorer.db.marshallers.columnMarshaller
import wafna.dbexplorer.db.marshallers.foreignKeyMarshaller
import wafna.dbexplorer.db.marshallers.indexMarshaller
import wafna.dbexplorer.db.marshallers.schemaMarshaller
import wafna.dbexplorer.db.marshallers.tableConstraintMarshaller
import wafna.dbexplorer.db.marshallers.tableMarshaller
import wafna.dbexplorer.db.marshallers.viewMarshaller
import wafna.dbexplorer.domain.Column
import wafna.dbexplorer.domain.ForeignKey
import wafna.dbexplorer.domain.Index
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.domain.Table
import wafna.dbexplorer.domain.TableConstraint
import wafna.dbexplorer.domain.View
import wafna.dbexplorer.domain.errors.DomainResult
import wafna.dbexplorer.util.LazyLogger

private val log = LazyLogger(MetaDAO::class)

context (Database)
internal fun createMetaDAO() = object : MetaDAO {
    override suspend fun listSchemas(): DomainResult<List<Schema>> = trap {
        log.selectLogged(
            """SELECT ${schemaMarshaller.project("ss")}
        |FROM information_schema.schemata ss""".trimMargin()
        ) { schemaMarshaller.read(it) }
    }

    override suspend fun listTables(schemaName: String): DomainResult<List<Table>> = trap {
        log.selectLogged(
            """SELECT ${tableMarshaller.project("ts")}
        |FROM information_schema.tables ts
        |WHERE ts.table_schema = ?""".trimMargin(),
            schemaName
        ) { tableMarshaller.read(it) }
    }

    override suspend fun getTable(schemaName: String, tableName: String): DomainResult<Table?> = trap {
        log.selectLogged(
            """SELECT ${tableMarshaller.project("ts")}
        |FROM information_schema.tables ts
        |WHERE ts.table_schema = ?
        |  AND ts.table_name = ?""".trimMargin(),
            schemaName,
            tableName
        ) { tableMarshaller.read(it) }.firstOrNull()
    }

    override suspend fun listViews(schemaName: String): DomainResult<List<View>> = trap {
        log.selectLogged(
            """SELECT ${viewMarshaller.project("vs")}
        |FROM information_schema.views vs
        |WHERE vs.table_schema = ?""".trimMargin(),
            schemaName
        ) { viewMarshaller.read(it) }
    }

    override suspend fun listColumns(schemaName: String, tableName: String): DomainResult<List<Column>> = trap {
        log.selectLogged(
            """SELECT ${columnMarshaller.project("cs")}
        |FROM information_schema.columns cs
        |WHERE cs.table_schema = ? AND cs.table_name = ?""".trimMargin(),
            schemaName,
            tableName
        ) { columnMarshaller.read(it) }
    }

    override suspend fun listTableConstraints(
        schemaName: String,
        tableName: String
    ): DomainResult<List<TableConstraint>> = trap {
        log.selectLogged(
            """SELECT ${tableConstraintMarshaller.project("tcs")}
        |FROM information_schema.table_constraints tcs
        |WHERE tcs.table_schema = ? AND tcs.table_name = ?""".trimMargin(),
            schemaName,
            tableName
        ) { tableConstraintMarshaller.read(it) }
    }

    override suspend fun listIndexes(schemaName: String, tableName: String): DomainResult<List<Index>> = trap {
        log.selectLogged(
            """SELECT ${indexMarshaller.project("ixs")}
            |FROM pg_indexes ixs
            |WHERE ixs.schemaname = ? AND ixs.tablename = ?""".trimMargin(),
            schemaName,
            tableName
        ) { indexMarshaller.read(it) }
    }

    override suspend fun listForeignKeys(schemaName: String, tableName: String): DomainResult<List<ForeignKey>> = trap {
        log.selectLogged(
            foreignKeys(FKDirection.FROM),
            schemaName,
            tableName
        ) { foreignKeyMarshaller.read(it) }
    }

    override suspend fun listForeignKeyRefs(schemaName: String, tableName: String): DomainResult<List<ForeignKey>> =
        trap {
            log.selectLogged(
                foreignKeys(FKDirection.TO),
                schemaName,
                tableName
            ) { foreignKeyMarshaller.read(it) }
        }
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
    |    AND ${dir.tableName}.table_name = ?""".trimMargin()
