package com.mkulikov.taskb.server.config

import com.mkulikov.taskb.server.route.groupRoutes
import com.mkulikov.taskb.server.route.testRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        testRoutes()
        groupRoutes()
    }
}