package com.mkulikov.taskb.server.route

import com.mkulikov.taskb.server.dto.CreateGroupRequest
import com.mkulikov.taskb.server.dto.GroupEntity
import com.mkulikov.taskb.server.dto.Groups
import com.mkulikov.taskb.server.dto.TaskEntity
import com.mkulikov.taskb.server.dto.Tasks
import com.mkulikov.taskb.server.dto.UserEntity
import io.ktor.http.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.groupRoutes() {
    route("/groups") {
        getAllGroups()
        addGroup()
        updateGroup()
        deleteGroup()
        deleteGroupWithTasks()
    }
}

private fun Route.getAllGroups() {
    get {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()

        val groups = transaction {
            GroupEntity.find { Groups.user eq userId }
                .map { it.toDto() }
        }
        call.respond(groups)
    }
}

private fun Route.addGroup() {
    post {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()

        val req = call.receive<CreateGroupRequest>()
        val dto = transaction {
            GroupEntity.new {
                name = req.name
                color = req.color
                user = UserEntity[userId]
            }.toDto()
        }
        call.respond(HttpStatusCode.Created, dto)
    }
}

private fun Route.updateGroup() {
    put("/{id}") {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid group id")

        val req = call.receive<CreateGroupRequest>()

        val updated = transaction {
            val group = GroupEntity.findById(id) ?: return@transaction null
            if (group.user.id.value != userId) return@transaction null

            group.name = req.name
            group.color = req.color
            group.toDto()
        } ?: return@put call.respond(HttpStatusCode.NotFound, "Group not found or access denied")

        call.respond(updated)
    }
}

private fun Route.deleteGroup() {
    delete("/{id}") {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid group id")

        val isDeleted = transaction {
            val group = GroupEntity.findById(id) ?: return@transaction false
            if (group.user.id.value != userId) return@transaction false

            TaskEntity.find { Tasks.group eq group.id }.forEach { task ->
                task.group = null
            }

            group.delete()
            true
        }

        if (!isDeleted) {
            call.respond(HttpStatusCode.NotFound, "Group not found or access denied")
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun Route.deleteGroupWithTasks() {
    delete("/{id}/with-tasks") {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.payload.getClaim("user_id").asInt()
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid group id")

        val isDeleted = transaction {
            val group = GroupEntity.findById(id) ?: return@transaction false
            if (group.user.id.value != userId) return@transaction false

            TaskEntity.find { Tasks.group eq group.id }.forEach { task ->
                task.delete()
            }

            group.delete()
            true
        }

        if (!isDeleted) {
            call.respond(HttpStatusCode.NotFound, "Group not found or access denied")
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
