package gridley

import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import react.FC
import react.Props
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState
import web.cssom.ClassName

/**
 * Definition of a column in the table, providing the facilities of rendering, searching, and
 * sorting. By specifying the columns in an indexable collection we can correlate them with table
 * events, like sorting. This abstraction later sets up a very generic way to render the table and
 * data as well as implementation of searching and sorting.
 */
abstract class DisplayColumn<R> {
    /**
     * Indicates whether and how the column can be searched. This allows for flexibility like
     * case-insensitive search or search on text implied by status icons.
     */
    abstract fun searchFunction(record: R): String?

    /**
     * Indicates whether and how the column can be sorted. Allows for other sorting independent of
     * representation, e.g. numbers and dates.
     */
    abstract val comparator: Comparator<R>?

    /** The rendering of a column header. Note that the sort key controls are applied later. */
    abstract val header: FC<Props>

    /** The rendering of a record field. Access to the entire record allows for derived fields. */
    abstract fun renderField(record: R): FC<Props>

    // The comparator can't have a default value and still be overrideable.
    val defaultComparator: Comparator<R> =
        Comparator { a, b ->
            val termA = searchFunction(a)
            val termB = searchFunction(b)
            if (termA == null) {
                if (termB == null) 0 else -1
            } else {
                if (termB == null) 1 else termA.compareTo(termB)
            }
        }
}

/** Keeping track of the column on and direction in which sorting is to be applied. */
data class SortKey(val index: Int, val sortDir: SortDir)

data class GridDisplay<R>(
    // We keep separate lists of filtered and sorted records.
    // This improves performance as well as making the effect of successive sorts cumulative.
    val filteredRecords: List<IndexedValue<R>>,
    val sortedRecords: List<IndexedValue<R>>,
    val pageCount: Int,
    val effectivePage: Int,
    val displayRecords: List<IndexedValue<R>>,
    val selectedPage: Int,
    val sortKey: SortKey?,
)

/** We can be type safe on the records because none of the-components knows what it's doing. */
external interface GridleyProps<R> : Props {
    var columns: List<DisplayColumn<R>>
    var pageSize: Int
    var recordSet: List<R>

    // The type checker misses when this is null, anyway, so it's optional with a reasonale default.
    var emptyMessage: FC<Props>?
}

/**
 * This is the nexus of the grid where all the bits are wired together.
 *
 * @param baseId used to calculate names and ids for components.
 */
fun <R> createGrid(baseId: String): FC<GridleyProps<R>> =
    FC { props ->
        // The indices will be the effective ids for the records.
        val gridRecords: List<IndexedValue<R>> = props.recordSet.withIndex().toList()

        var display by useState(
            GridDisplay(
                filteredRecords = gridRecords,
                sortedRecords = gridRecords,
                pageCount = 1,
                effectivePage = 0,
                displayRecords = gridRecords,
                selectedPage = 0,
                sortKey = null
            )
        )

        // This is the final update so it updates the state.
        fun updateRecordPage(gridDisplay: GridDisplay<R>) {
            val totalRecords = gridDisplay.filteredRecords.size
            val pageCount = ceil(totalRecords.toDouble() / props.pageSize).toInt()
            // Ensure we're on an actual page.
            val effectivePage = max(0, min(pageCount - 1, gridDisplay.selectedPage))
            // The page of records to display.
            val displayRecords =
                if (1 >= gridDisplay.sortedRecords.size) {
                    gridDisplay.sortedRecords
                } else {
                    gridDisplay.sortedRecords.run {
                        val low = max(0, effectivePage * props.pageSize)
                        val high = min((1 + effectivePage) * props.pageSize, totalRecords)
                        slice(low until high)
                    }
                }
            display =
                gridDisplay.copy(
                    pageCount = pageCount, effectivePage = effectivePage, displayRecords = displayRecords,
                )
        }

        fun updateSortedRecords(
            gridDisplay: GridDisplay<R>,
            sortKey: SortKey?,
        ) {
            val sortedRecords =
                when (sortKey) {
                    null -> gridDisplay.filteredRecords
                    else -> {
                        // We can be sure the comparator exists because we rendered a sort key for it.
                        props.columns[sortKey.index].comparator!!.let { comparator ->
                            // Ignores the index.
                            val indexedComparator =
                                Comparator<IndexedValue<R>> { a, b -> comparator.compare(a.value, b.value) }
                            gridDisplay.filteredRecords.sortedWith(
                                when (sortKey.sortDir) {
                                    SortDir.Ascending -> indexedComparator
                                    SortDir.Descending -> indexedComparator.reversed()
                                },
                            )
                        }
                    }
                }
            updateRecordPage(gridDisplay.copy(sortedRecords = sortedRecords, sortKey = sortKey))
        }

        fun updateFilteredRecords(
            gridDisplay: GridDisplay<R>,
            searchTarget: String,
        ) {
            val filteredRecords =
                props.recordSet.withIndex().filter { record ->
                    props.columns.any { column ->
                        val searchValue = column.searchFunction(record.value)
                        true == searchValue?.contains(searchTarget)
                    }
                }
            val updatedDisplay = gridDisplay.copy(filteredRecords = filteredRecords)
            updateSortedRecords(updatedDisplay, gridDisplay.sortKey)
        }

        useEffectOnce { updateFilteredRecords(display, "") }

        div {
            className = ClassName("row")
            div {
                className = ClassName("col-lg-12")
                div {
                    className = ClassName("grid-control-box")
                    div {
                        className = ClassName("grid-control")
                        GridleyPager {
                            totalPages = display.pageCount
                            currentPage = display.effectivePage
                            onPageSelect = { updateRecordPage(display.copy(selectedPage = it)) }
                        }
                    }
                    div {
                        className = ClassName("grid-control")
                        Search {
                            this.baseId = baseId
                            onSearch = { searchTarget -> updateFilteredRecords(display, searchTarget) }
                        }
                    }
                }
            }
        }
        br {}
        div {
            className = ClassName("row")
            div {
                className = ClassName("col-lg-12")
                GridleyDisplay {
                    // Render the column headers to an array of components.
                    headers =
                        props.columns.withIndex().map { p ->
                            val columnIndex = p.index
                            val column = p.value
                            FC {
                                div {
                                    className = ClassName("grid-column-header-box")
                                    // The presence of a comparator means the column is sortable.
                                    if (null != column.comparator) {
                                        div {
                                            className = ClassName("grid-column-header")
                                            SortControl {
                                                sortDir =
                                                    display.sortKey?.let {
                                                        if (columnIndex == it.index) it.sortDir else null
                                                    }
                                                action = { sortDir ->
                                                    updateSortedRecords(display, SortKey(columnIndex, sortDir))
                                                }
                                            }
                                        }
                                    }
                                    div {
                                        className = ClassName("grid-column-header")
                                        column.header {}
                                    }
                                }
                            }
                        }
                    records =
                        display.displayRecords.map { record ->
                            RecordLine(
                                key = record.index.toString(),
                                displayLine = props.columns.map { it.renderField(record.value) },
                            )
                        }
                    emptyMessage = props.emptyMessage ?: FC { +"No records found." }
                }
            }
        }
    }
