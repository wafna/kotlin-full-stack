package wafna.dbexplorer.db.marshallers

import com.google.common.base.CaseFormat
import wafna.database.FieldNameConverter

val appDbFieldNameConverter = object : FieldNameConverter {
    override fun toColumnName(name: String): String =
        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
}
