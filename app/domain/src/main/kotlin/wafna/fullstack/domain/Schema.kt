package wafna.fullstack.domain

data class Schema(
    val catalogName: String,
    val schemaName: String,
    val schemaOwner: String,
    val defaultCharacterSetCatalog: String?,
    val defaultCharacterSetSchema: String?,
    val defaultCharacterSetName: String?,
    val sqlPath: String?
)
