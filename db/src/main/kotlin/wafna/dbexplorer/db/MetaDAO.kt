@file:Suppress("import-ordering")

package wafna.dbexplorer.db

import arrow.core.getOrElse
import arrow.core.toOption
import java.sql.ResultSet
import wafna.dbexplorer.db.marshallers.schemaMarshaller
import wafna.dbexplorer.domain.Column
import wafna.dbexplorer.domain.Constraint
import wafna.dbexplorer.domain.Index
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.domain.Table
import wafna.dbexplorer.domain.View


private val tableProjection = listOf(
    "table_catalog", "table_schema", "table_name", "table_type", "self_referencing_column_name", "reference_generation",
    "user_defined_type_catalog", "user_defined_type_schema", "user_defined_type_name", "is_insertable_into", "is_typed",
    "commit_action"
)

private val viewProjection = listOf(
    "table_catalog", "table_schema", "table_name", "view_definition", "check_option", "is_updatable",
    "is_insertable_into", "is_trigger_updatable", "is_trigger_deletable", "is_trigger_insertable_into"
)

private val columnProjection = listOf(
    "table_catalog", "table_schema", "table_name", "column_name", "ordinal_position",
    "data_type", "column_default", "is_nullable"
)

private val constraintProjection = listOf(
    "constraint_catalog", "constraint_schema", "constraint_name", "table_catalog", "table_schema", "table_name",
    "constraint_type", "is_deferrable", "initially_deferred", "enforced", "nulls_distinct"
)

private val indexProjection = listOf(
    "schemaname", "tablename", "indexname", "tablespace", "indexdef"
)

private operator fun List<String>.invoke(t: String? = null) = t.toOption()
    .map { prefix ->
        map { "$prefix.$it" }
    }.getOrElse { this }.joinToString(", ")

context (Database)
internal fun createMetaDAO(): MetaDAO = object : MetaDAO {
    override suspend fun listSchemas(): List<Schema> = select(
        """SELECT ${schemaMarshaller()}
          |FROM information_schema.schemata""".trimMargin()
    ) { schemaMarshaller(it) }

    override suspend fun listTables(schemaName: String): List<Table> = select(
        """SELECT ${tableProjection()}
          |FROM information_schema.tables
          |WHERE table_schema = ?""".trimMargin(),
        schemaName
    ) { it.readTable() }

    override suspend fun getTable(schemaName: String, tableName: String): Table? = select(
        """SELECT ${tableProjection()}
            |FROM information_schema.tables
            |WHERE table_schema = ?
            |  AND table_name = ?""".trimMargin(),
        schemaName, tableName
    ) { it.readTable() }.firstOrNull()

    override suspend fun listViews(schemaName: String): List<View> = select(
        """SELECT ${viewProjection()}
          |FROM information_schema.views
          |WHERE table_schema = ?""".trimMargin(),
        schemaName
    ) { it.readView() }

    override suspend fun listColumns(schemaName: String, tableName: String): List<Column> = select(
        """SELECT ${columnProjection()}
          |FROM information_schema.columns
          |WHERE table_schema = ? AND table_name = ?""".trimMargin(),
        schemaName, tableName
    ) { it.readColumn() }

    override suspend fun listConstraints(schemaName: String, tableName: String): List<Constraint> = select(
        """SELECT ${constraintProjection()}
          |FROM information_schema.table_constraints
          |WHERE table_schema = ? AND table_name = ?""".trimMargin(),
        schemaName, tableName
    ) { it.readConstraint() }

    override suspend fun listIndexes(schemaName: String, tableName: String): List<Index> = select(
        """SELECT ${indexProjection()}
          |FROM pg_indexes
          |WHERE schemaname = ? AND tablename = ?""".trimMargin(),
        schemaName, tableName
    ) { it.readIndex() }
}

internal fun ResultSet.readTable() = Table(
    tableCatalog = getString("table_catalog"),
    tableSchema = getString("table_schema"),
    tableName = getString("table_name"),
    tableType = getString("table_type"),
    selfReferencingColumnName = getString("self_referencing_column_name"),
    referenceGeneration = getString("reference_generation"),
    userDefinedTypeCatalog = getString("user_defined_type_catalog"),
    userDefinedTypeSchema = getString("user_defined_type_schema"),
    userDefinedTypeName = getString("user_defined_type_name"),
    isInsertableInto = getBoolean("is_insertable_into"),
    isTyped = getBoolean("is_typed"),
    commitAction = getString("commit_action"),
)

internal fun ResultSet.readView() = View(
    tableCatalog = getString("table_catalog"),
    tableSchema = getString("table_schema"),
    tableName = getString("table_name"),
    viewDefinition = getString("view_definition"),
    checkOption = getString("check_option"),
    isUpdatable = getBoolean("is_updatable"),
    isInsertableInto = getBoolean("is_insertable_into"),
    isTriggerUpdatable = getBoolean("is_trigger_updatable"),
    isTriggerDeletable = getBoolean("is_trigger_deletable"),
    isTriggerInsertableInto = getBoolean("is_trigger_insertable_into"),
)

internal fun ResultSet.readColumn() = Column(
    tableCatalog = getString("table_catalog"),
    tableSchema = getString("table_schema"),
    tableName = getString("table_name"),
    columnName = getString("column_name"),
    ordinalPosition = getInt("ordinal_position"),
    dataType = getString("data_type"),
    columnDefault = getString("column_default"),
    isNullable = getBoolean("is_nullable"),
)

internal fun ResultSet.readConstraint() = Constraint(
    constraintCatalog = getString("constraint_catalog"),
    constraintSchema = getString("constraint_schema"),
    constraintName = getString("constraint_name"),
    tableCatalog = getString("table_catalog"),
    tableSchema = getString("table_schema"),
    tableName = getString("table_name"),
    constraintType = getString("constraint_type"),
    isDeferrable = getBoolean("is_deferrable"),
    initiallyDeferred = getBoolean("initially_deferred"),
    enforced = getBoolean("enforced"),
    nullsDistinct = getBoolean("nulls_distinct"),
)

internal fun ResultSet.readIndex() = Index(
    schemaName = getString("schemaname"),
    tableName = getString("tablename"),
    indexName = getString("indexname"),
    tableSpace = getString("tablespace"),
    indexDef = getString("indexdef"),
)
