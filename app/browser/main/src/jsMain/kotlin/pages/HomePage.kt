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
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.summary
import react.useEffectOnce
import react.useState
import util.Loading
import util.XData
import util.preventDefault
import util.targetFiles
import util.targetString
import util.withIO
import web.cssom.ClassName
import web.dom.ElementId
import web.file.File
import web.html.InputType
import web.html.file
import web.html.text
import web.prompts.alert

/**
 * This is the nexus of the grid where all the bits are wired together.
 */
val HomePage = FC<Props> {
    var pageResult: XData<HomePageData> by useState(XData.Ready)

    fun updatePageResult() = withIO {
        pageResult = XData(Api.data.homePage())
    }

    useEffectOnce {
        updatePageResult()
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
    details {
        summary {
            span { +"Import Data Block" }
        }
        CreateDataBlock {
            onSuccess = { updatePageResult() }
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
                    override fun searchFunction(record: DataBlock): String = record.name.lowercase()
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
//                object : DisplayColumnLongDate<DataBlock>("Closed") {
//                    override fun value(record: DataBlock): Long? = record.deletedAt
//                },
            )
    }
}

private external interface CreateDataBlockProps : Props {
    var onSuccess: (DataBlock) -> Unit
}

private val CreateDataBlock = FC<CreateDataBlockProps> { props ->
    var dataBlockName by useState("")
    var importFile: File? by useState(null)
    var inFlight by useState(false)
    form {
        div {
            className = ClassName("form-group")
            label {
                htmlFor = ElementId("data-block-name")
                +"Name"
            }
            input {
                className = ClassName("form-control")
                type = InputType.text
                id = ElementId("data-block-name")
                name = "data-block-name"
                value = dataBlockName
                onChange = { dataBlockName = it.targetString }
            }
        }
        div {
            className = ClassName("form-group")
            label {
                htmlFor = ElementId("data-block-file")
                +"File"
            }
            input {
                className = ClassName("inline form-control-file")
                id = ElementId("import-snapshot-file")
                name = "import-snapshot-file"
                type = InputType.file
                onChange = { importFile = it.targetFiles[0] }
                multiple = false
                accept = ".csv"
            }
        }
        val enabled = !inFlight && dataBlockName.isNotEmpty() && importFile != null
        button {
            className = ClassName("btn btn-primary")
            disabled = !enabled
            +"Create"
            onClick = preventDefault {
                inFlight = true
                withIO {
                    Api.data.import(dataBlockName, importFile!!).apply {
                        inFlight = false
                        onSuccess { dataBlock ->
                            props.onSuccess(dataBlock)
                            alert("\u263A Success!")
                        }
                        onFailure { e ->
                            console.error("Import failed.", Exception(e))
                            alert("Import failed.")
                        }
                    }
                    dataBlockName = ""
                }
            }
        }
    }
}
