package pages

import client.Api
import defaultErrorHandler
import domain.DataBlockRecords
import plots.Histogram
import plots.HistogramDisplay
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState
import util.Loading
import util.XData
import util.withIO
import web.cssom.ClassName

external interface DataBlockViewProps : Props {
    var dataBlockId: String
}

val DataBlockView = FC<DataBlockViewProps> { props ->
    var pageData: XData<DataBlockRecords> by useState(XData.Ready)

    fun updatePageData() {
        withIO {
            pageData = XData.Loading
            pageData = RuntimeException(props.dataBlockId).let {
                XData(Api.data.dataBlock(props.dataBlockId))
            }

        }
    }

    useEffectOnce {
        updatePageData()
    }

    pageData
        .onLoading { Loading() }
        .onReceived { result ->
            defaultErrorHandler(result) { data ->
                val dataBlock = data.dataBlock
                div {
                    className = ClassName("data-block-name")
                    +dataBlock.name
                }
                val records = data.records
                if (records.isEmpty()) {
                    div {
                        className = ClassName("alert alert-warning")
                        +"No records."
                    }
                } else {
                    for (record in records) {
                        Histogram {
                            title = record.key
                            scores = record.data.mapNotNull { it.toDoubleOrNull() }
                            display = HistogramDisplay.Vigesile
                        }
                    }
                }
            }
        }
}