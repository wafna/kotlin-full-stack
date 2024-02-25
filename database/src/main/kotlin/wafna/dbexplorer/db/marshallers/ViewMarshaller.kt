package wafna.dbexplorer.db.marshallers

import wafna.kdbc.Marshaller
import wafna.dbexplorer.domain.View
import java.sql.ResultSet

val viewMarshaller = object : Marshaller<View>() {
    override val columnNames: List<String> = listOf(
        "table_catalog", "table_schema", "table_name", "view_definition", "check_option", "is_updatable",
        "is_insertable_into", "is_trigger_updatable", "is_trigger_deletable", "is_trigger_insertable_into"
    )

    override fun read(resultSet: ResultSet): View = resultSet.run {
        View(
            tableCatalog = getString("table_catalog"),
            tableSchema = getString("table_schema"),
            tableName = getString("table_name"),
            viewDefinition = getString("view_definition"),
            checkOption = getString("check_option"),
            isUpdatable = getBoolean("is_updatable"),
            isInsertableInto = getBoolean("is_insertable_into"),
            isTriggerUpdatable = getBoolean("is_trigger_updatable"),
            isTriggerDeletable = getBoolean("is_trigger_deletable"),
            isTriggerInsertableInto = getBoolean("is_trigger_insertable_into")
        )
    }
}
