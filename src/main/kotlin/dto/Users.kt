package com.mkulikov.taskb.server.dto

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID

object Users : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)  // BCrypt хеш ~60 символов
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var username by Users.username
    var passwordHash by Users.passwordHash

    fun toDto() = UserDto(id.value, username)
}

@Serializable
data class UserDto(val id: Int, val username: String)

@Serializable
data class CreateUserRequest(val username: String, val password: String)

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String)