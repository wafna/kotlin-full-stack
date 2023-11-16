package gridley

import react.FC
import react.Props
import react.dom.aria.ariaLabel
import react.useState
import util.withTargetValue
import web.cssom.ClassName
import web.html.InputType
import react.dom.html.ReactHTML as h

external interface GridleySearchProps : Props {
    var onSearch: (String) -> Unit
}

val Search = FC<GridleySearchProps> { props ->
    var filter by useState("")
    h.input {
        className = ClassName("form-control")
        type = InputType.search
        placeholder = "Search..."
        ariaLabel = "Search"
        value = filter
        size = 48
        onChange = withTargetValue {
            filter = it
            props.onSearch(it)
        }
        autoFocus = true
    }
}
