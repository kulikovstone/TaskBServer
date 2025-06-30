package com.mkulikov.taskb.server.route

import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

private const val CODE_VERSION = "1.0"

fun Route.testRoutes() {
    testServerStatus()
    testDbStatus()
}

private fun Route.testServerStatus() {
    get("/test") {
        call.respondText("TaskB server is running...\nVersion: $CODE_VERSION")
    }
}

private fun Route.testDbStatus() {
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
}
