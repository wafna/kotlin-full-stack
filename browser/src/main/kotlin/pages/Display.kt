package pages

import gridley.DisplayColumn
import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.ClassName

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
