package com.mkulikov.taskb.server.route

import com.mkulikov.taskb.server.dto.CreateGroupRequest
import com.mkulikov.taskb.server.dto.GroupEntity
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.groupRoutes() {
    getAllGroups()
    addGroup()
}

private fun Route.getAllGroups() {
    get("/groups") {
        val groups = transaction {
            GroupEntity.all().map { it.toDto() }
        }
        call.respond(groups)
    }
}

private fun Route.addGroup() {
    post("/groups") {
        val request = call.receive<CreateGroupRequest>()
        val created = transaction {
            GroupEntity.new {
                name = request.name
                color = request.color
            }.toDto()
        }
        call.respond(HttpStatusCode.Created, created)
    }
}
