import kotlinx.browser.window
import kotlinx.coroutines.launch
import pages.SchemaListView
import pages.TableDetailView
import pages.TableListView
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

object SchemasRoute : Route {
    override val routeId: String = "schemas"
    override fun component(params: Params): FC<Props> = SchemaListView
}

enum class ParamNames(val value: String) {
    Schema("schema"),
    Table("table");

    operator fun invoke(): String = value
}

object TablesRoute : Route {
    override val routeId: String = "tables"
    override fun component(params: Params): FC<Props> = FC {
        TableListView {
            schemaName = params[ParamNames.Schema()]!!
        }
    }

    fun makeHash(schemaName: String): HashRoute = HashRoute(routeId, mapOf(ParamNames.Schema() to schemaName))
}

object TableDetailRoute : Route {
    override val routeId: String = "tableDetail"
    override fun component(params: Params): FC<Props> = FC {
        TableDetailView {
            schemaName = params[ParamNames.Schema()]!!
            tableName = params[ParamNames.Table()]!!
        }
    }

    fun makeHash(schemaName: String, tableName: String): HashRoute =
        HashRoute(routeId, ParamNames.Schema() to schemaName, ParamNames.Table() to tableName)
}

val Routes = listOf(SchemasRoute, TablesRoute, TableDetailRoute)

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

//        Row {
//            Col {
//                scale = ColumnScale.Large
//                size = 12
//                NavBar {
//                    h.a {
//                        className = ClassName("navbar-brand")
//                        href = SchemasRoute.defaultHash().href
//                        +"Schemas"
//                    }
//                }
//            }
//        }
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
