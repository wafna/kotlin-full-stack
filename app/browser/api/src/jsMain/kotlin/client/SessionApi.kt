package client

import domain.AuthResult
import domain.User

interface SessionApi {
    suspend fun login(username: String): Result<AuthResult>
    suspend fun whoami(): Result<AuthResult>
    suspend fun logout(): Result<Unit>
}

fun sessionApi(baseUrl: String, segmentPath: String): SessionApi =
    object : SessionApi, ApiSegment(baseUrl, segmentPath) {
        override suspend fun login(username: String): Result<AuthResult> =
            get(segmentUrl("login", "username" to username)).json()

        override suspend fun whoami(): Result<AuthResult> =
            get(segmentUrl("whoami")).json()

        override suspend fun logout(): Result<Unit> =
            get(segmentUrl("logout")).unit
    }
