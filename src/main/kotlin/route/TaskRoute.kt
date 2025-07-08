package com.mkulikov.taskb.server.route

import com.mkulikov.taskb.server.dto.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.taskRoutes() {
    route("/tasks") {
        getAllTasks()
        addTask()
        updateTask()
        deleteTask()
    }
}

private fun Route.getAllTasks() {
    get {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()
        val tasks = transaction {
            TaskEntity.find { Tasks.user eq userId }
                .map { it.toDto() }
        }
        call.respond(tasks)
    }
}

private fun Route.addTask() {
    post {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()
        val req = call.receive<CreateTaskRequest>()

        val dto = transaction {
            TaskEntity.new {
                name = req.name
                description = req.description
                createdAt = req.createdAt
                targetDate = req.targetDate
                completedAt = null
                user = UserEntity[userId]
                group = req.groupId?.let { GroupEntity[it] }
            }.toDto()
        }

        call.respond(HttpStatusCode.Created, dto)
    }
}

private fun Route.updateTask() {
    put("/{id}") {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid task id")

        val req = call.receive<UpdateTaskRequest>()

        val updated = transaction {
            val task = TaskEntity.findById(id) ?: return@transaction null
            if (task.user.id.value != userId) return@transaction null

            req.name?.let { task.name = it }
            req.description?.let { task.description = it }
            req.targetDate?.let { task.targetDate = it }
            task.completedAt = req.completedAt
            task.group = req.groupId?.let { GroupEntity[it] }

            task.toDto()
        } ?: return@put call.respond(HttpStatusCode.NotFound, "Task not found or access denied")

        call.respond(updated)
    }
}

private fun Route.deleteTask() {
    delete("/{id}") {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid task id")

        val isDeleted = transaction {
            val task = TaskEntity.findById(id) ?: return@transaction false
            if (task.user.id.value != userId) return@transaction false

            task.delete()
            true
        }

        if (!isDeleted) {
            call.respond(HttpStatusCode.NotFound, "Task not found or access denied")
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
