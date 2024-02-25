package wafna.dbexplorer.db.marshallers

import wafna.database.genericMarshaller
import wafna.dbexplorer.domain.Column

/**
 * An example of using the GenericMarshaller.
 */
val columnMarshaller = genericMarshaller<Column>(appDbFieldNameConverter)
