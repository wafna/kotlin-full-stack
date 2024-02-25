package wafna.dbexplorer.db.marshallers

import wafna.kdbc.Marshaller
import wafna.dbexplorer.domain.Schema
import java.sql.ResultSet

val schemaMarshaller = object : Marshaller<Schema>() {
    override val columnNames: List<String> = listOf(
        "catalog_name",
        "schema_name",
        "schema_owner",
        "default_character_set_catalog",
        "default_character_set_schema",
        "default_character_set_name",
        "sql_path"
    )

    override fun read(resultSet: ResultSet): Schema = resultSet.run {
        Schema(
            catalogName = getString("catalog_name"),
            schemaName = getString("schema_name"),
            schemaOwner = getString("schema_owner"),
            defaultCharacterSetCatalog = getString("default_character_set_catalog"),
            defaultCharacterSetSchema = getString("default_character_set_schema"),
            defaultCharacterSetName = getString("default_character_set_name"),
            sqlPath = getString("sql_path")
        )
    }
}
