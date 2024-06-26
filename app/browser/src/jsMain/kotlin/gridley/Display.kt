package gridley

import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

/**
 * A list of components to be embedded in a row in a table.
 */
typealias DisplayLine = List<FC<Props>>

/**
 * The components for a row of the table plus a key for React.
 */
data class RecordLine(val key: String, val displayLine: DisplayLine)

external interface GridleyDisplayProps : Props {
    /**
     * List of components for the table header.
     */
    var headers: DisplayLine

    /**
     * A list of lists of components for the table data.
     */
    var records: List<RecordLine>

    /**
     * Component to display when there are no records.
     * This gives us the option of displaying the empty table.
     */
    var emptyMessage: FC<Props>
}

/**
 * Creates a table with the supplied className and renders the header and row data into it.
 */
val GridleyDisplay = FC<GridleyDisplayProps> { props ->

    val records = props.records

    h.div {
        h.div {
            className = ClassName("flow-down")
            h.table {
                className = ClassName("table table-sm gridley-header")
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
        if (records.isEmpty()) {
            props.emptyMessage {}
        }
    }
}

abstract class DisplayColumnStdHdr<R>(headerText: String) : DisplayColumn<R>() {
    override val header: FC<Props> = FC {
        ReactHTML.span {
            className = ClassName("gridley-header")
            +headerText
        }
    }
    override val comparator: Comparator<R> = defaultComparator
}

abstract class DisplayColumnInt<R>(headerText: String) : DisplayColumnStdHdr<R>(headerText) {
    abstract fun value(record: R): Int
    override val searchFunction: ((R) -> String) = { value(it).toString() }
    override val comparator: Comparator<R> = Comparator { a, b -> value(a).compareTo(value(b)) }
    override fun renderField(record: R): FC<Props> = FC { ReactHTML.pre { +value(record).toString() } }
}

abstract class DisplayColumnPre<R>(headerText: String) : DisplayColumnStdHdr<R>(headerText) {
    abstract fun value(record: R): String
    override val searchFunction: ((R) -> String) = { value(it) }
    override val comparator: Comparator<R> = Comparator { a, b -> value(a).compareTo(value(b)) }
    override fun renderField(record: R): FC<Props> = FC { ReactHTML.pre { +value(record) } }
}
