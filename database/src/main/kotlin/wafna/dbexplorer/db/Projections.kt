package wafna.dbexplorer.db

import com.google.common.base.CaseFormat
import wafna.dbexplorer.domain.Column
import wafna.dbexplorer.domain.ForeignKey
import wafna.dbexplorer.domain.Index
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.domain.Table
import wafna.dbexplorer.domain.TableConstraint
import wafna.dbexplorer.domain.View
import wafna.kdbc.FieldNameConverter
import wafna.kdbc.projection

private val fieldNameConverter = object : FieldNameConverter {
    override fun toColumnName(name: String): String =
        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
}

object Projections {
    val schemas = projection<Schema>("information_schema.schemata", fieldNameConverter)
    val tables = projection<Table>("information_schema.tables", fieldNameConverter)
    val columns = projection<Column>("information_schema.columns", fieldNameConverter)
    val indexes = projection<Index>(
        "pg_indexes", listOf(
            "schemaname",
            "tablename",
            "indexname",
            "tablespace",
            "indexdef"
        )
    )
    val tableConstraints = projection<TableConstraint>("information_schema.table_constraints", fieldNameConverter)
    val foreignKeys = projection<ForeignKey>(
        "information_schema.foreign_keys", listOf(
            "schema_name",
            "table_name",
            "constraint_name",
            "column_name",
            "foreign_schema_name",
            "foreign_table_name",
            "foreign_column_name"
        )
    )
    val views = projection<View>("information_schema.views", fieldNameConverter)
}
