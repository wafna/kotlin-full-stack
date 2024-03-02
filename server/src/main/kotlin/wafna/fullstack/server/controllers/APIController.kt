package wafna.fullstack.server.controllers

import arrow.core.raise.either
import wafna.fullstack.domain.Schema
import wafna.fullstack.domain.Table
import wafna.fullstack.domain.errors.DomainError
import wafna.fullstack.domain.errors.DomainResult
import wafna.fullstack.server.ServerContext
import wafna.fullstack.server.views.TableView

interface APIController {
    suspend fun overview(): DomainResult<List<Schema>>
    suspend fun schema(schemaName: String): DomainResult<List<Table>>
    suspend fun table(schemaName: String, tableName: String): DomainResult<TableView>
}

context(ServerContext)
internal fun apiController() = object : APIController {
    override suspend fun overview(): DomainResult<List<Schema>> =
        db.meta.listSchemas()

    override suspend fun schema(schemaName: String): DomainResult<List<Table>> =
        db.meta.listTables(schemaName)

    override suspend fun table(schemaName: String, tableName: String): DomainResult<TableView> = either {
        val table = db.meta.getTable(schemaName, tableName).bind()
            ?: raise(DomainError.NotFound(""))
        val columns = db.meta.listColumns(schemaName, tableName).bind()
        val tableConstraints = db.meta.listTableConstraints(schemaName, tableName).bind()
            .filter { !it.constraintName.contains("_not_null") }
        val foreignKeys = db.meta.listForeignKeys(schemaName, tableName).bind()
        val foreignKeyRefs = db.meta.listForeignKeyRefs(schemaName, tableName).bind()
        val indexes = db.meta.listIndexes(schemaName, tableName).bind()
        TableView(table, columns, tableConstraints, foreignKeys, foreignKeyRefs, indexes)
    }
}
