import kotlinx.browser.window
import kotlinx.coroutines.launch
import pages.OverviewView
import pages.SchemaView
import pages.TableView
import react.FC
import react.Props
import react.useEffectOnce
import react.useState
import util.Col
import util.ColumnScale
import util.Container
import util.HashRoute
import util.Params
import util.Route
import util.Row
import util.doRoute
import react.dom.html.ReactHTML as h

// Routes.

object OverviewRoute : Route {
    override val routeId: String = "overview"
    override fun component(params: Params): FC<Props> = OverviewView
}

enum class ParamNames(val value: String) {
    Schema("schema"),
    Table("table");

    operator fun invoke(): String = value
}

object SchemaRoute : Route {
    override val routeId: String = "schema"
    override fun component(params: Params): FC<Props> = FC {
        SchemaView {
            schemaName = params[ParamNames.Schema()]!!
        }
    }

    fun makeHash(schemaName: String): HashRoute = HashRoute(routeId, mapOf(ParamNames.Schema() to schemaName))
}

object TableRoute : Route {
    override val routeId: String = "table"
    override fun component(params: Params): FC<Props> = FC {
        TableView {
            schemaName = params[ParamNames.Schema()]!!
            tableName = params[ParamNames.Table()]!!
        }
    }

    fun makeHash(schemaName: String, tableName: String): HashRoute =
        HashRoute(routeId, ParamNames.Schema() to schemaName, ParamNames.Table() to tableName)
}

val Routes = listOf(OverviewRoute, SchemaRoute, TableRoute)

// Main component.

/**
 * Containing the chrome and the routing.
 */
val App = FC<Props> {

    var route: HashRoute? by useState(null)

    fun updateRoute() = mainScope.launch {
        route = HashRoute.currentHash()
    }

    useEffectOnce {
        window.onhashchange = { updateRoute() }
        updateRoute()
    }

    Container {
        Row {
            Col {
                scale = ColumnScale.Large
                size = 12
                h.h1 {
                    +"DB Explorer"
                    onClick = { updateRoute() }
                }
            }
        }

        h.br()
        Row {
            Col {
                scale = ColumnScale.Large
                size = 12
                doRoute(route, Routes)
            }
        }
    }
}
