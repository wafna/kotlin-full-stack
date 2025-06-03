import react.ChildrenBuilder
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

fun <T> ChildrenBuilder.defaultErrorHandler(result: Result<T>, success: (T) -> Unit) {
    try {
        success(result.getOrThrow())
    } catch (e: Throwable) {
        console.error(e)
        div {
            className = ClassName("alert alert-danger")
            +e.toString()
        }
    }
}
