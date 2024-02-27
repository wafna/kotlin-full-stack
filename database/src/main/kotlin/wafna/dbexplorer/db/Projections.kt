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
    val schemaMarshaller = projection<Schema>("information_schema.schemata", fieldNameConverter)
    val tableMarshaller = projection<Table>("information_schema.tables", fieldNameConverter)
    val columnMarshaller = projection<Column>("information_schema.columns", fieldNameConverter)
    val indexMarshaller = projection<Index>("pg_indexes", fieldNameConverter)
    val foreignKeyMarshaller = projection<ForeignKey>("information_schema.foreign_keys", fieldNameConverter)
    val tableConstraintMarshaller =
        projection<TableConstraint>("information_schema.table_constraints", fieldNameConverter)
    val viewMarshaller = projection<View>("information_schema.views", fieldNameConverter)
}
