import react.create
import react.dom.client.createRoot
import web.dom.document

fun main() {
    document.getElementById("root")?.also {
        createRoot(it).render(App.create())
    } ?: error("Couldn't find root container!")
}
