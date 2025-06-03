package gridley

import util.preventDefault
import react.FC
import react.Props
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

enum class SortDir {
    Ascending,
    Descending,
}

/** A clickable control for a single sort direction. */
fun sortIcon(text: String) =
    FC<Props> {
        h.span {
            className = ClassName("float-down clickable grid-spinner")
            +text
        }
    }

external interface SortControlProps : Props {
    /**
     * Which, if either, of the arrows to highlight. This indicates the current sort condition on the
     * row.
     */
    var sortDir: SortDir?

    /**
     * When one of the directions is selected the direction is transmitted to the controller, which
     * already knows which column this is.
     */
    var action: (SortDir) -> Unit
}

/** A clickable control for bidirectional sorting. */
val SortControl =
    FC<SortControlProps> { props ->
        val sort = props.sortDir
        h.div {
            className = ClassName("inline grid-sort-control-box")
            h.div {
                onClick = preventDefault {
                    props.action(SortDir.Ascending)
                }
                when (sort) {
                    SortDir.Ascending -> sortIcon("▲")()
                    else -> sortIcon("△")()
                }
            }
            h.div {
                onClick = preventDefault {
                    props.action(SortDir.Descending)
                }
                when (sort) {
                    SortDir.Descending -> sortIcon("▼")()
                    else -> sortIcon("▽")()
                }
            }
        }
    }
