package wafna.dbexplorer.db.marshallers

import wafna.kdbc.Marshaller
import wafna.dbexplorer.domain.TableConstraint
import java.sql.ResultSet

val tableConstraintMarshaller = object : Marshaller<TableConstraint>() {
    override val columnNames: List<String> = listOf(
        "constraint_catalog", "constraint_schema", "constraint_name", "table_catalog", "table_schema", "table_name",
        "constraint_type", "is_deferrable", "initially_deferred", "enforced", "nulls_distinct"
    )

    override fun read(resultSet: ResultSet): TableConstraint = resultSet.run {
        TableConstraint(
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
            nullsDistinct = getBoolean("nulls_distinct")
        )
    }
}
