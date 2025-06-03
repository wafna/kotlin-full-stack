package plots

import util.Entities
import kotlin.math.roundToLong
import react.FC
import react.Props
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

internal fun Double.format() = ((this * 100).roundToLong() / 100.0).toString()

external interface StatsBoxProps : Props {
    var headers: List<Pair<String, String>>
    var value: String
    var stats: Stats
}

internal val StatsBoxVert = FC<StatsBoxProps> { props ->
    h.table {
        className = ClassName("table table-sm table-bordered")
        fun box(name: String, value: String) {
            h.tr {
                h.th { +name }
                h.td { +value }
            }
        }
        for ((p, q) in props.headers) {
            box(p, q)
        }
        val status = props.stats
        box("N", status.n.toString())
        box("Min", status.min.toString())
        box("Max", status.max.toString())
        box("Average", status.avg.format())
        box("Median", status.med.format())
        box("Std Dev", status.stdDev.format())
    }
}
