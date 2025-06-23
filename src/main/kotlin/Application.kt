package com.mkulikov.taskb.server

import com.mkulikov.taskb.server.db.DatabaseFactory
import com.mkulikov.taskb.server.route.groupRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        DatabaseFactory.init()
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

        // Ендпойнт для проверки базы данных
        get("/db-check") {
            try {
                transaction {
                    exec("SELECT 1")
                }
                call.respondText("DB connection OK ✅")
            } catch (e: Exception) {
                call.respondText("DB connection FAILED ❌: ${e.message}")
            }
        }

        groupRoutes()
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