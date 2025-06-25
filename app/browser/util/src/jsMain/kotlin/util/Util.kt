package util

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.PropsWithChildren
import react.PropsWithClassName
import react.PropsWithStyle
import react.dom.events.ChangeEvent
import react.dom.events.SyntheticEvent
import web.file.FileList
import web.html.HTMLInputElement

private val mainScope = MainScope()

// Used throughout to wrap api calls and effects.
fun <T> withIO(block: suspend () -> T) {
    mainScope.launch { block() }
}

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
fun <T : SyntheticEvent<*, *>> preventDefault(op: (T) -> Unit): (T) -> Unit = { e ->
    e.preventDefault()
    op(e)
}

fun withTargetValue(block: (String) -> Unit): (ChangeEvent<HTMLInputElement>) -> Unit =
    { e ->
        block(e.target.value)
    }

/**
 * Receives the value of an input when it changes.
 */
val ChangeEvent<HTMLInputElement>.targetString: String
    get() = target.asDynamic().value as String

val ChangeEvent<HTMLInputElement>.targetFiles: FileList
    get() = target.files!!

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

/**
 * Models the metastate of data fetched via API calls (external data).
 */
sealed class XData<out T> {
    abstract fun onReady(f: () -> Unit): XData<T>
    abstract fun onLoading(f: () -> Unit): XData<T>
    abstract fun onReceived(f: (Result<T>) -> Unit): XData<T>
    abstract fun onSuccess(f: (T) -> Unit): XData<T>
    abstract fun withSuccess(f: T.() -> Unit): XData<T>
    abstract fun onFailure(f: (Throwable) -> Unit): XData<T>

    object Ready : XData<Nothing>() {
        override fun onReady(f: () -> Unit) = apply { f() }
        override fun onLoading(f: () -> Unit) = this
        override fun onReceived(f: (Result<Nothing>) -> Unit) = this
        override fun onSuccess(f: (Nothing) -> Unit): XData<Nothing> = this
        override fun withSuccess(f: Nothing.() -> Unit): XData<Nothing> = this
        override fun onFailure(f: (Throwable) -> Unit): XData<Nothing> = this
    }

    object Loading : XData<Nothing>() {
        override fun onReady(f: () -> Unit) = this
        override fun onLoading(f: () -> Unit) = apply { f() }
        override fun onReceived(f: (Result<Nothing>) -> Unit) = this
        override fun onSuccess(f: (Nothing) -> Unit): XData<Nothing> = this
        override fun withSuccess(f: Nothing.() -> Unit): XData<Nothing> = this
        override fun onFailure(f: (Throwable) -> Unit): XData<Nothing> = this
    }

    class Received<T>(val result: Result<T>) : XData<T>() {
        override fun onReady(f: () -> Unit) = this
        override fun onLoading(f: () -> Unit) = this
        override fun onReceived(f: (Result<T>) -> Unit) = apply { f(result) }
        override fun onSuccess(f: (T) -> Unit): XData<T> = apply { result.onSuccess { f(it) } }
        override fun withSuccess(f: T.() -> Unit): XData<T> = apply { result.onSuccess { with(it) { f() } } }
        override fun onFailure(f: (Throwable) -> Unit): XData<T> = apply { result.onFailure { f(it) } }
    }

    companion object {
        operator fun <T> invoke(result: Result<T>): XData<T> = Received(result)
    }
}
