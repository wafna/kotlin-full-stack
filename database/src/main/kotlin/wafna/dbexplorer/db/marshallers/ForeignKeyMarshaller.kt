package wafna.dbexplorer.db.marshallers

import wafna.kdbc.Marshaller
import wafna.dbexplorer.domain.ForeignKey

val foreignKeyMarshaller = object : Marshaller<ForeignKey>() {
    override val columnNames: List<String> = listOf(
        "table_schema",
        "constraint_name",
        "table_name",
        "column_name",
        "foreign_table_schema",
        "foreign_table_name",
        "foreign_column_name"
    )

    override fun read(resultSet: java.sql.ResultSet): ForeignKey = resultSet.run {
        ForeignKey(
            schemaName = getString("table_schema"),
            tableName = getString("table_name"),
            constraintName = getString("constraint_name"),
            columnName = getString("column_name"),
            foreignSchemaName = getString("foreign_table_schema"),
            foreignTableName = getString("foreign_table_name"),
            foreignColumnName = getString("foreign_column_name")
        )
    }
}
