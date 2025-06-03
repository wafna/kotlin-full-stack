import pages.DataBlockView
import pages.HomePage
import react.FC
import react.Props
import util.HashRoute
import util.Params
import util.Route

enum class ParamNames(val value: String) {
    DataBlockId("data-block-id");

    operator fun invoke(): String = value
}

object HomeRoute : Route {
    override val routeId: String = "home"
    override fun component(params: Params): FC<Props> = HomePage
}

object DataBlockRoute : Route {
    override val routeId: String = "data-block"
    override fun component(params: Params): FC<Props> = FC {
        DataBlockView {
            dataBlockId = params[ParamNames.DataBlockId()]!!
        }
    }

    fun makeHash(dataBlockId: String): HashRoute = HashRoute(routeId, mapOf(ParamNames.DataBlockId() to dataBlockId))
}

val Routes =
    listOf(
        HomeRoute,
        DataBlockRoute
    ).apply {
        require(isNotEmpty()) { "No routes provided." }
        require(fold(emptySet<String>()) { p, q -> p + q.routeId }.size == size) {
            "Non-unique route ids detected."
        }
    }

