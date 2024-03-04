package wafna.fullstack.db

import wafna.fullstack.db.Projections.columns
import wafna.fullstack.db.Projections.foreignKeys
import wafna.fullstack.db.Projections.indexes
import wafna.fullstack.db.Projections.schemas
import wafna.fullstack.db.Projections.tableConstraints
import wafna.fullstack.db.Projections.tables
import wafna.fullstack.db.Projections.views
import wafna.fullstack.domain.Column
import wafna.fullstack.domain.ForeignKey
import wafna.fullstack.domain.Index
import wafna.fullstack.domain.Schema
import wafna.fullstack.domain.Table
import wafna.fullstack.domain.TableConstraint
import wafna.fullstack.domain.View
import wafna.fullstack.domain.errors.DomainResult
import wafna.kdbc.Database
import wafna.kdbc.select
import wafna.kdbc.selectCustom

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
        transact {
            select(schemas, "ss", "")
        }
    }

    override suspend fun listTables(
        schemaName: String
    ): DomainResult<List<Table>> = domainResult {
        transact {
            select(
                tables, "ts",
                "WHERE ts.table_schema = ?",
                schemaName
            )
        }
    }

    override suspend fun getTable(
        schemaName: String,
        tableName: String
    ): DomainResult<Table?> = domainResult {
        transact {
            select(
                tables, "ts",
                "WHERE ts.table_schema = ? AND ts.table_name = ?",
                schemaName,
                tableName
            ).firstOrNull()
        }
    }

    override suspend fun listViews(
        schemaName: String
    ): DomainResult<List<View>> = domainResult {
        transact {
            select(
                views, "vs",
                "WHERE vs.table_schema = ?",
                schemaName
            )
        }
    }

    override suspend fun listColumns(
        schemaName: String,
        tableName: String
    ): DomainResult<List<Column>> = domainResult {
        transact {
            select(
                columns, "cs",
                "WHERE cs.table_schema = ? AND cs.table_name = ?",
                schemaName,
                tableName
            )
        }
    }

    override suspend fun listTableConstraints(
        schemaName: String,
        tableName: String
    ): DomainResult<List<TableConstraint>> = domainResult {
        transact {
            select(
                tableConstraints, "tcs",
                "WHERE tcs.table_schema = ? AND tcs.table_name = ?",
                schemaName,
                tableName
            )
        }
    }

    override suspend fun listIndexes(
        schemaName: String,
        tableName: String
    ): DomainResult<List<Index>> = domainResult {
        transact {
            select(
                indexes, "ixs",
                "WHERE ixs.schemaname = ? AND ixs.tablename = ?",
                schemaName,
                tableName
            )
        }
    }

    override suspend fun listForeignKeys(
        schemaName: String,
        tableName: String
    ): DomainResult<List<ForeignKey>> = domainResult {
        transact {
            selectCustom(
                foreignKeys,
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
        transact {
            selectCustom(
                foreignKeys,
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
    |    tc.table_schema schema_name, 
    |    tc.table_name, 
    |    tc.constraint_name, 
    |    kcu.column_name, 
    |    ccu.table_schema AS foreign_schema_name,
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
