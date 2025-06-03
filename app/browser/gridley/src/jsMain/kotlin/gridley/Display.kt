package gridley

import kotlin.js.Date
import react.FC
import react.Props
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

/** A list of components to be embedded in a row in a table. */
typealias DisplayLine = List<FC<Props>>

/** The components for a row of the table plus a key for React. */
data class RecordLine(val key: String, val displayLine: DisplayLine)

external interface GridleyDisplayProps : Props {
    /** List of components for the table header. */
    var headers: DisplayLine

    /** A list of lists of components for the table data. */
    var records: List<RecordLine>

    /**
     * Component to display when there are no records. This gives us the option of displaying the
     * empty table.
     */
    var emptyMessage: FC<Props>
}

/** Creates a table with the supplied className and renders the header and row data into it. */
val GridleyDisplay =
    FC<GridleyDisplayProps> { props ->
        val records = props.records

        h.div {
            h.div {
                if (records.isEmpty()) {
                    props.emptyMessage {}
                } else {
                    h.table {
                        className = ClassName("table table-sm")
                        h.thead {
                            h.tr {
                                for (header in props.headers) {
                                    h.th { header {} }
                                }
                            }
                        }
                        h.tbody {
                            for (record in records) {
                                h.tr {
                                    key = record.key
                                    for (component in record.displayLine) {
                                        h.td { component {} }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

abstract class DisplayColumnDefaults<R, E : Comparable<E>> : DisplayColumn<R>() {
    abstract fun value(record: R): E?

    override fun searchFunction(record: R): String? = value(record)?.toString()

    override val comparator: Comparator<R> =
        Comparator { a, b ->
            val a1 = value(a)
            val b1 = value(b)
            if (a1 == null) {
                if (b1 == null) 0 else -1
            } else {
                if (b1 == null) 1 else a1.compareTo(b1)
            }
        }

    override fun renderField(record: R): FC<Props> = FC { h.pre { +value(record).toString() } }
}

abstract class DisplayColumnStdHdr<R, E : Comparable<E>>(headerText: String) :
    DisplayColumnDefaults<R, E>() {
    override val header: FC<Props> =
        FC {
            h.span {
                 +headerText
            }
        }
    override val comparator: Comparator<R> = defaultComparator
}

abstract class DisplayColumnPre<R>(headerText: String) :
    DisplayColumnStdHdr<R, String>(headerText) {
    override fun renderField(record: R): FC<Props> = FC { h.pre { +value(record) } }
}

abstract class DisplayColumnText<R>(headerText: String) :
    DisplayColumnStdHdr<R, String>(headerText)

abstract class DisplayColumnLongDate<R>(headerText: String) :
    DisplayColumnStdHdr<R, Long>(headerText) {
    override fun renderField(record: R): FC<Props> =
        FC {
            +(value(record)?.let { Date(it).toDateString() } ?: "")
        }
}

abstract class DisplayColumnLongDateTime<R>(headerText: String) :
    DisplayColumnStdHdr<R, Long>(headerText) {
    fun displayText(record: R): String = value(record)?.let { Date(it).toDateString() } ?: ""

    override fun renderField(record: R): FC<Props> = FC { +(displayText(record)) }
}
