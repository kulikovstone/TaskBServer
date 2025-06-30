package com.mkulikov.taskb.server.config

import com.mkulikov.taskb.server.route.groupRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

private const val CODE_VERSION = "1.0"

fun Application.configureRouting() {
    routing {
        get("/test") {
            call.respondText("TaskB server is running...\nVersion: $CODE_VERSION")
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