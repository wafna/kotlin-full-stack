import domain.Schema
import domain.Table
import domain.TableDetail
import util.get
import util.json
import util.makeURL

object API {
    private const val apiRoot = "http://localhost:8686/api/"
    private fun makeURL(path: String, vararg params: Pair<String, String>): String =
        makeURL(apiRoot, path, *params)

    suspend fun listSchemas(): List<Schema> =
        json(get(makeURL("overview")))

    suspend fun listTables(schemaName: String): List<Table> =
        json(get(makeURL("schema/${schemaName}")))

    suspend fun tableDetail(schemaName: String, tableName: String): TableDetail =
        json(get(makeURL("table/${schemaName}/${tableName}")))
}
