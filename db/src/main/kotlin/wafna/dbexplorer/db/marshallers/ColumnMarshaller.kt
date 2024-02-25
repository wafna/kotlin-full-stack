package wafna.dbexplorer.db.marshallers

import wafna.dbexplorer.domain.Column
import wafna.database.genericMarshaller

/**
 * An example of using the GenericMarshaller.
 */
val columnMarshaller = genericMarshaller<Column>(appDbFieldNameConverter)
