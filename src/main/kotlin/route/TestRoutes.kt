package com.mkulikov.taskb.server.route

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.transactions.transaction

private val serverStart: Instant = Clock.System.now()

fun Route.testRoutes() {
    testServerStatus()
    testDbStatus()
}

private fun Route.testServerStatus() {
    get("/test") {
        val now = Clock.System.now()
        val diffMillis = now.toEpochMilliseconds() - serverStart.toEpochMilliseconds()
        val totalSeconds = diffMillis / 1000

        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        call.respondText(
            """
            TaskB server is running…
            Uptime: ${hours}h ${minutes}m ${seconds}s
            """.trimIndent()
        )
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
