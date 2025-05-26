package com.mkulikov.taskb.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureLogging()
        configureRouting()
    }.start(wait = true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureLogging() {
    install(CallLogging)
}

fun Application.configureRouting() {
    routing {
        get("/hello") {
            call.respondText("Hello world")
        }

        post("/greet") {
            val name = call.receive<String>()
            call.respondText("Hello, $name!")
        }

        post("/greet-json") {
            val request = call.receive<GreetRequest>()
            call.respond(GreetResponse(greet = "Hello, ${request.name}!"))
        }
    }
}

@Serializable
data class GreetRequest(
    val name: String
)

@Serializable
data class GreetResponse(
    val greet: String
)