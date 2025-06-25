import client.Api
import domain.AuthResult
import kotlinx.browser.document
import kotlinx.browser.window
import react.FC
import react.Props
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.dialog
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.h3
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
import web.html.HTMLDialogElement
import web.html.InputType

// Main component.

/**
 * Containing the chrome and the routing.
 */
val App = FC<Props> {
    var route: HashRoute? by useState(null)
    var authResult: XData<AuthResult> by useState(XData.Ready)
    var error: Throwable? by useState(null)

    fun updateRoute() = withIO {
        route = HashRoute.currentHash()
    }

    fun login(result: Result<AuthResult>) {
        authResult = XData(result)
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
                    // This could come from the initial check or the form.
                    with(authResult) {
                        onFailure {
                            console.error(it)
                            error = it
                            LoginForm {
                                setAuthResult = ::login
                            }
                        }
                        withSuccess {
                            if (null != user) {
                                doRoute(route, Routes)
                            } else {
                                if (null != error) {
                                    div {
                                        className = ClassName("alert alert-danger")
                                    }
                                }
                                LoginForm {
                                    setAuthResult = ::login
                                }
                                h3 { +"Welcome to this demonstration project." }
                                button {
                                    className = ClassName("btn btn-primary")
                                    +"Login"
                                    onClick = preventDefault {
                                        val loginForm = document.getElementById("login-form") as HTMLDialogElement
                                        loginForm.showModal()
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

private const val LoginFormId = "login-form"

external interface LoginFormProps : Props {
    var setAuthResult: (Result<AuthResult>) -> Unit
}

val LoginForm = FC<LoginFormProps> { props ->
    var username: String by useState("")
    var authResult: XData<AuthResult> by useState(XData.Ready)
    var error: String? by useState(null)
    fun login() {
        withIO {
            authResult = XData(Api.session.login(username))
        }
    }
    dialog {
        id = LoginFormId

        with(authResult) {
            onLoading { Loading() }
            onFailure { e ->
                error = e.message ?: "An error has occurred."
            }
            onSuccess { result ->
                if (null == result.user) {
                    error = "Unknown username."
                    username = ""
                    authResult = XData.Ready
                } else {
                    withIO {
                        props.setAuthResult(Result.success(result))
                    }
                }
            }
            onReady {
                form {
                    onSubmit = preventDefault { login() }
                    div {
                        className = ClassName("form-group")
                        label {
                            htmlFor = "username"
                            +"Username"
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
                if (null != error) {
                    div {
                        className = ClassName("alert alert-danger")
                        +error
                    }
                }
            }
        }
    }
}

