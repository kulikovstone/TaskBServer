package com.mkulikov.taskb.server

import com.mkulikov.taskb.server.config.configureLogging
import com.mkulikov.taskb.server.config.configureRouting
import com.mkulikov.taskb.server.config.configureSecurity
import com.mkulikov.taskb.server.config.configureSerialization
import com.mkulikov.taskb.server.db.DatabaseFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()
        configureSerialization()
        configureLogging()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}