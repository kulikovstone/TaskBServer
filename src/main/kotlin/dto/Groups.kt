package com.mkulikov.taskb.server.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.EntityID

object Groups : IntIdTable("groups") {
    val name = varchar("name", 255)
    val color = integer("color")
    val user = reference("user_id", Users)
}

class GroupEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupEntity>(Groups)

    var name by Groups.name
    var color by Groups.color
    var user by UserEntity referencedOn Groups.user

    fun toDto() = GroupDto(
        id = id.value,
        name = name,
        color = color
    )
}

@Serializable
data class GroupDto(
    val id: Int,
    val name: String,
    val color: Int,
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val color: Int
)