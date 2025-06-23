package com.mkulikov.taskb.server.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.EntityID

object Groups : IntIdTable("groups") {
    val name = varchar("name", 255)
    val color = varchar("color", 9) // hex format: "#RRGGBBAA"
}

class GroupEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupEntity>(Groups)

    var name by Groups.name
    var color by Groups.color

    fun toDto() = GroupDto(id.value, name, color)
}

@Serializable
data class GroupDto(
    val id: Int,
    val name: String,
    val color: String
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val color: String
)