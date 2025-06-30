package com.mkulikov.taskb.server.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity() {
    val secret = System.getenv("JWT_SECRET") ?: error("JWT_SECRET not set")
    val issuer = System.getenv("JWT_ISSUER") ?: error("JWT_ISSUER not set")
    val audience = System.getenv("JWT_AUDIENCE") ?: error("JWT_AUDIENCE not set")

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "taskb"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                if (username != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}