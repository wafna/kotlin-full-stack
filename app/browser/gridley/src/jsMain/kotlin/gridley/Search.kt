package gridley

import util.withTargetValue
import react.FC
import react.Props
import react.useState
import web.cssom.ClassName
import web.html.InputType
import react.dom.html.ReactHTML as h

external interface GridleySearchProps : Props {
    var baseId: String
    var onSearch: (String) -> Unit
}

val Search =
    FC<GridleySearchProps> { props ->
        var filter by useState("")
        h.input {
            className = ClassName("form-control")
            type = InputType.search
            id = "${props.baseId}-search"
            name = "${props.baseId}-search"
            placeholder = "Search…"
            ariaLabel = "Search"
            value = filter
            size = 48
            onChange =
                withTargetValue {
                    filter = it
                    props.onSearch(it)
                }
            autoFocus = true
        }
    }
