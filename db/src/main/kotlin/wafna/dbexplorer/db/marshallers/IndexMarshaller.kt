package wafna.dbexplorer.db.marshallers

import wafna.database.Marshaller
import wafna.dbexplorer.domain.Index
import java.sql.ResultSet

val indexMarshaller = object : Marshaller<Index>() {
    override val fields: List<String> = listOf(
        "schemaname",
        "tablename",
        "indexname",
        "tablespace",
        "indexdef"
    )

    override fun read(resultSet: ResultSet): Index = resultSet.run {
        Index(
            schemaName = getString("schemaname"),
            tableName = getString("tablename"),
            indexName = getString("indexname"),
            tableSpace = getString("tablespace"),
            indexDef = getString("indexdef")
        )
    }
}
