import client.Api
import domain.AuthResult
import kotlinx.browser.window
import react.FC
import react.Props
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.small
import react.useEffectOnce
import react.useState
import util.Col
import util.ColumnScale
import util.Container
import util.HashRoute
import util.Loading
import util.Row
import util.XData
import util.doRoute
import util.preventDefault
import util.targetString
import util.withIO
import web.cssom.ClassName
import web.html.InputType

// Main component.

/**
 * Containing the chrome and the routing.
 */
val App = FC<Props> {
    var route: HashRoute? by useState(null)
    var authResult: XData<AuthResult> by useState(XData.Ready)

    fun updateRoute() = withIO {
        route = HashRoute.currentHash()
    }

    fun login(result: Result<AuthResult>) {
        authResult = XData.Received(result)
    }

    useEffectOnce {
        updateRoute()
        window.onhashchange = { updateRoute() }
        withIO {
            authResult = XData.Loading
            authResult = XData.Received(Api.session.whoami())
        }
    }

    Container {
        Row {
            Col {
                scale = ColumnScale.Large
                size = 12
                div {
                    className = ClassName("app-title-bar")
                    div {
                        className = ClassName("app-title-text")
                        img {
                            src = "./favicon-32x32.png"
                            alt = "Kotlin Fullstack"
                        }
                        +"Kotlin Fullstack"
                        onClick = preventDefault { window.location.href = "/" }
                    }
                    authResult.withSuccess {
                        if (null != user) {
                            div {
                                className = ClassName("app-title-user-header")
                                div {
                                    +"Welcome, ${user!!.username}"
                                }
                                div {
                                    small {
                                        className = ClassName("clickable")
                                        +"[logout]"
                                        onClick = preventDefault {
                                            withIO {
                                                authResult = XData.Received(Result.success(AuthResult(null)))
                                                Api.session.logout().onFailure { console.error(it) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Row {
            Col {
                scale = ColumnScale.Large
                size = 12
                div {
                    // Do nothing for the Ready state.
                    authResult.onLoading {
                        Loading()
                    }.onFailure {
                        console.error(it)
                        LoginForm {
                            setAuthResult = ::login
                        }
                        div {
                            className = ClassName("alert alert-danger")
                            +"Login failed."
                        }
                    }.withSuccess {
                        if (null != user) {
                            doRoute(route, Routes)
                        } else {
                            LoginForm {
                                setAuthResult = ::login
                            }
                        }
                    }
                }
            }
        }
    }
}


external interface LoginFormProps : Props {
    var setAuthResult: (Result<AuthResult>) -> Unit
}

val LoginForm = FC<LoginFormProps> { props ->
    var username: String by useState("")
    var loading by useState { false }
    fun login() {
        withIO {
            loading = true
            props.setAuthResult(Api.session.login(username))
            loading = false
        }
    }
    if (loading)
        Loading()
    else
        form {
            onSubmit = preventDefault { login() }
            div {
                className = ClassName("form-group")
                label {
                    htmlFor = "username"
                    +"Login"
                }
                input {
                    className = ClassName("form-control")
                    id = "username"
                    name = "username"
                    type = InputType.text
                    placeholder = "Username"
                    value = username
                    autoFocus = true
                    onChange = { username = it.targetString }
                }
            }
            br()
            div {
                className = ClassName("form-group")
                button {
                    className = ClassName("btn btn-primary")
                    disabled = username.isBlank()
                    onClick = preventDefault { login() }
                    +"Login"
                }
            }
        }
}

