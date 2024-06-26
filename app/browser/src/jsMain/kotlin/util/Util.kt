package util

import react.PropsWithChildren
import react.PropsWithClassName
import react.PropsWithStyle
import react.dom.events.ChangeEvent
import react.dom.events.MouseEvent
import web.cssom.ClassName
import web.html.HTMLInputElement

/**
 * There is no JSX style way to push a component's properties down, en masse.
 * Instead, derive this for component props classes and push the properties, like so:
 * <code>
 *     className = props.className
 *     style = props.style
 *     children = props.children
 * <code>
 */
external interface PropsSplat : PropsWithChildren, PropsWithStyle, PropsWithClassName

/**
 * Wraps an event handler by calling <code>preventDefault()</code> on the event before passing it on.
 * This is especially useful for buttons.
 */
fun preventDefault(op: (MouseEvent<*, *>) -> Unit): (MouseEvent<*, *>) -> Unit = { e ->
    e.preventDefault()
    op(e)
}

/**
 * Receives the value of an input when it changes.
 */
fun withTargetValue(block: (String) -> Unit): (ChangeEvent<HTMLInputElement>) -> Unit = { e ->
    block(e.target.value)
}

// By allowing nulls we can conditionally inline CSS classes.
fun classNames(vararg className: String?): ClassName =
    ClassName(className.filterNotNull().joinToString(" "))

fun makeURL(apiRoot: String, path: String, vararg params: Pair<String, String>): String = buildString {
    append(apiRoot)
    append(path)
    if (params.isNotEmpty()) {
        append("?")
        var sep = false
        for (param in params) {
            if (sep) append("&") else sep = true
            append(param.first)
            append("=")
            append(param.second)
        }
    }
}
