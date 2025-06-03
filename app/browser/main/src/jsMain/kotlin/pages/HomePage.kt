package pages

import DataBlockRoute
import client.Api
import defaultErrorHandler
import domain.DataBlock
import domain.HomePageData
import gridley.DisplayColumnLongDate
import gridley.DisplayColumnText
import gridley.createGrid
import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState
import util.Loading
import util.XData
import util.withIO
import web.cssom.ClassName

/**
 * This is the nexus of the grid where all the bits are wired together.
 */
val HomePage = FC<Props> {
    var pageResult: XData<HomePageData> by useState(XData.Ready)

    useEffectOnce {
        withIO {
            pageResult = XData(Api.data.homePage())
        }
    }

    pageResult
        .onLoading {
            Loading()
        }.onReceived { result ->
            defaultErrorHandler(result) { data ->
                if (data.dataBlocks.isEmpty()) {
                    div {
                       className = ClassName("alert alert-warning")
                       +"No data are available."
                    }
                } else {
                    createGrid(data.dataBlocks)
                }
            }
        }
}

private fun ChildrenBuilder.createGrid(dataBlocks: List<DataBlock>) {
    (createGrid<DataBlock>("sections")) {
        recordSet = dataBlocks
        pageSize = 15
        columns =
            listOf(
                object : DisplayColumnText<DataBlock>("Name") {
                    override fun value(record: DataBlock): String = record.name
                    override fun searchFunction(record: DataBlock): String? = record.name.lowercase()
                    override fun renderField(record: DataBlock): FC<Props> =
                        FC {
                            a {
                                val route = DataBlockRoute.makeHash(record.id).href
                                href = route
                                +record.name
                            }
                        }
                },
                object : DisplayColumnLongDate<DataBlock>("Created") {
                    override fun value(record: DataBlock): Long = record.createdAt
                },
                object : DisplayColumnLongDate<DataBlock>("Closed") {
                    override fun value(record: DataBlock): Long? = record.deletedAt
                },
            )
    }
}