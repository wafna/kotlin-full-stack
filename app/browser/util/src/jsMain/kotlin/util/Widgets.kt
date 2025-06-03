package util

import react.FC
import react.Props
import react.PropsWithChildren
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ul
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

val Loading = FC<Props> {
    img {
        src = "loading.gif"
        height = 64.0
        height = 64.0
    }
}

external interface NavItemProps : Props {
    var name: String
    var to: HashRoute
}

val Container = FC<PropsSplat> { props ->
    div {
        className = props.className
        className = ClassName("container")
        style = props.style
        children = props.children
    }
}

val Row = FC<PropsSplat> { props ->
    div {
        className = props.className
        className = ClassName("row")
        style = props.style
        children = props.children
    }
}

@Suppress("unused")
enum class ColumnScale(private val scale: String) {
    Small("sm"),
    Medium("md"),
    Large("lg");

    override fun toString(): String = scale
}

external interface ColProps : PropsSplat {
    var scale: ColumnScale
    var size: Int
}

val Col = FC<ColProps> { props ->
    div {
        //className = props.className
        className = ClassName("col-${props.scale}-${props.size}")
        style = props.style
        children = props.children
    }
}
