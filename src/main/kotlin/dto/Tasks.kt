package com.mkulikov.taskb.server.dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Tasks : IntIdTable("tasks") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val createdAt = datetime("created_at")
    val targetDate = date("target_date").nullable()
    val completedAt = datetime("completed_at").nullable()
    val group = reference("group_id", Groups).nullable()
    val user = reference("user_id", Users)
}

class TaskEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskEntity>(Tasks)

    var name by Tasks.name
    var description by Tasks.description
    var createdAt by Tasks.createdAt
    var targetDate by Tasks.targetDate
    var completedAt by Tasks.completedAt
    var group by GroupEntity optionalReferencedOn Tasks.group
    var user by UserEntity referencedOn Tasks.user

    fun toDto() = TaskDto(
        id = id.value,
        name = name,
        description = description,
        createdAt = createdAt,
        targetDate = targetDate,
        completedAt = completedAt,
        groupId = group?.id?.value,
        userId = user.id.value
    )
}

@Serializable
data class TaskDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime,
    val targetDate: LocalDate? = null,
    val completedAt: LocalDateTime? = null,
    val groupId: Int? = null,
    val userId: Int
)

@Serializable
data class CreateTaskRequest(
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime,
    val targetDate: LocalDate? = null,
    val groupId: Int? = null
)

@Serializable
data class UpdateTaskRequest(
    val name: String? = null,
    val description: String? = null,
    val targetDate: LocalDate? = null,
    val completedAt: LocalDateTime? = null,
    val groupId: Int? = null
)