package com.mkulikov.taskb.server.config

import com.mkulikov.taskb.server.dto.UserDto
import com.mkulikov.taskb.server.route.authRoutes
import com.mkulikov.taskb.server.route.groupRoutes
import com.mkulikov.taskb.server.route.testRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        testRoutes()
        authRoutes()
        groupRoutes()

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId     = principal.payload.getClaim("userId").asInt()
                val username   = principal.payload.getClaim("username").asString()
                call.respond(UserDto(userId, username))
            }
        }
    }
}