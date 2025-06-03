package plots

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.browser.document
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.letsPlot
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useEffect
import react.useState
import util.Col
import util.ColumnScale
import util.Container
import util.Row
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

internal external interface BarchartProps : Props {
    var name: String
    var abscissa: Pair<String, List<String>>
    var ordinate: Pair<String, List<Double>>
}

@ExperimentalUuidApi
internal val BarChart = FC<BarchartProps> { props ->
    val id = Uuid.random().toString()
    useEffect {
        val contentDiv = document.getElementById(id)?.apply {
            innerHTML = ""
        } ?: error("Content DIV not found: $id")
        val data = mapOf(props.abscissa, props.ordinate)
        val plot = letsPlot(data) + geomBar(
            stat = Stat.identity,
            color = "dark-green",
            fill = "green",
            alpha = .3,
            size = 1.0
        ) {
            x = props.abscissa.first
            y = props.ordinate.first
        }

        val plotDiv = JsFrontendUtil.createPlotDiv(plot)
        contentDiv.appendChild(plotDiv)
    }
    h.div { this.id = id }
}

enum class HistogramDisplay {
    Decile, Vigesile, Auto6, Auto8, Auto10, Auto12
}

external interface HistogramProps : Props {
    var title: String
    var scores: Iterable<Double>
    var display: HistogramDisplay?
}

@OptIn(ExperimentalUuidApi::class)
val Histogram = FC<HistogramProps> { props ->
    var display by useState<HistogramDisplay>(props.display ?: HistogramDisplay.Decile)
    val scores = props.scores.toList()
    if (scores.isEmpty()) {
        div {
            className = ClassName("alert alert-warning")
            +"No data."
        }
    } else {
        val buckets = when (display) {
            HistogramDisplay.Decile -> fixedBuckets(10, scores)
            HistogramDisplay.Vigesile -> fixedBuckets(20, scores)
            HistogramDisplay.Auto6 -> autoBuckets(6, scores)
            HistogramDisplay.Auto8 -> autoBuckets(8, scores)
            HistogramDisplay.Auto10 -> autoBuckets(10, scores)
            HistogramDisplay.Auto12 -> autoBuckets(12, scores)
        }
        div {
            className = ClassName("border padded-border")
            Container {
                Row {
                    Col {
                        scale = ColumnScale.Large
                        size = 6
                        div {
                            className = ClassName("histogram-title-bar")
                            div {
                                className = ClassName("histogram-title-element")
                                span {
                                    className = ClassName("record-title")
                                    +props.title
                                }
                            }
                            div {
                                className = ClassName("histogram-title-element")
                                span {
                                    h.div {
                                        className = ClassName("form-group")
                                        h.span { +"Display: " }
                                        h.select {
                                            HistogramDisplay.entries.forEach { bucket ->
                                                h.option {
                                                    val (value: Int, label: String) = when (bucket) {
                                                        HistogramDisplay.Decile -> -10 to "Decile"
                                                        HistogramDisplay.Vigesile -> -20 to "Vigesile"
                                                        HistogramDisplay.Auto6 -> 6 to "Auto 6"
                                                        HistogramDisplay.Auto8 -> 8 to "Auto 8"
                                                        HistogramDisplay.Auto10 -> 10 to "Auto 10"
                                                        HistogramDisplay.Auto12 -> 12 to "Auto 12"
                                                    }
                                                    this.value = value
                                                    this.label = label
                                                    if (bucket == display) {
                                                        selected = true
                                                    }
                                                }
                                                onChange = { e ->
                                                    display = when (e.target.value.toIntOrNull()) {
                                                        -10 -> HistogramDisplay.Decile
                                                        -20 -> HistogramDisplay.Vigesile
                                                        6 -> HistogramDisplay.Auto6
                                                        8 -> HistogramDisplay.Auto8
                                                        10 -> HistogramDisplay.Auto10
                                                        12 -> HistogramDisplay.Auto12
                                                        else -> HistogramDisplay.Auto8
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Row {
                    Col {
                        scale = ColumnScale.Large
                        size = 6
                        BarChart {
                            abscissa = "Scores" to buckets.scores
                            ordinate = "Count" to buckets.counts.map { it.toDouble() }
                        }
                    }
                    Col {
                        scale = ColumnScale.Large
                        size = 2
                        StatsBoxVert {
                            headers = emptyList()
                            stats = Stats.fromDataSet(props.scores)
                        }
                    }
                }
            }
        }
    }
}

data class Buckets(val scores: List<String>, val counts: List<Int>)

fun fixedBuckets(count: Int, data: Iterable<Double>): Buckets {
    require(0 < count) { "Count must be positive: $count" }
    val counts = Array(count) { 0 }
    val segment = 100.0 / count
    data.forEach { score ->
        for (i in 1..count) {
            if (score <= segment * i) {
                ++counts[i - 1]
                break
            }
        }
    }
    val scores = (1..count).map { i -> "${segment * (i - 1)}-${segment * i}" }
    return Buckets(scores, counts.toList())
}

fun autoBuckets(numBuckets: Int, data: Iterable<Double>): Buckets {
    require(numBuckets > 0) { "numBuckets must be > 0" }
    val sorted = data.sorted()
    if (sorted.isEmpty()) return Buckets(emptyList(), emptyList())
    val lo = floor(sorted.first())
    val hi = ceil(sorted.last())
    val range = hi - lo
    val scale = range / numBuckets
    val counts = Array(numBuckets) { 0 }
    data.forEach { score ->
        for (i in 1..numBuckets) {
            if (score <= lo + (scale * i)) {
                ++counts[i - 1]
                break
            }
        }
    }
    val scores = (1..numBuckets).map { i ->
        "${(lo + (scale * (i - 1))).format()}-${(lo + (scale * i)).format()}"
    }
    return Buckets(scores, counts.toList())
}