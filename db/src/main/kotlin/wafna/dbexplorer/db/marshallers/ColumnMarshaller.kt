package wafna.dbexplorer.db.marshallers

import wafna.database.Marshaller
import wafna.dbexplorer.domain.Column
import java.sql.ResultSet

val columnMarshaller = object : Marshaller<Column>() {
    override val fields: List<String> = listOf(
        "table_catalog",
        "table_schema",
        "table_name",
        "column_name",
        "ordinal_position",
        "data_type",
        "column_default",
        "is_nullable"
    )

    override fun read(resultSet: ResultSet): Column = resultSet.run {
        Column(
            tableCatalog = getString("table_catalog"),
            tableSchema = getString("table_schema"),
            tableName = getString("table_name"),
            columnName = getString("column_name"),
            ordinalPosition = getInt("ordinal_position"),
            dataType = getString("data_type"),
            columnDefault = getString("column_default"),
            isNullable = getBoolean("is_nullable")
        )
    }
}
