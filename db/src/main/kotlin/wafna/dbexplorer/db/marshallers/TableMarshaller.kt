package wafna.dbexplorer.db.marshallers

import wafna.dbexplorer.db.Marshaller
import wafna.dbexplorer.domain.Table
import java.sql.ResultSet

val tableMarshaller = object : Marshaller<Table>() {
    override val fields: List<String> = listOf(
        "table_catalog",
        "table_schema",
        "table_name",
        "table_type",
        "self_referencing_column_name",
        "reference_generation",
        "user_defined_type_catalog",
        "user_defined_type_schema",
        "user_defined_type_name",
        "is_insertable_into",
        "is_typed",
        "commit_action"
    )

    override fun read(resultSet: ResultSet): Table = resultSet.run {
        Table(
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
            commitAction = getString("commit_action")
        )
    }
}
