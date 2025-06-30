package com.mkulikov.taskb.server.route

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mkulikov.taskb.server.dto.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.authRoutes() {
    register()
    login()
}

private fun Route.register() {
    post("/register") {
        val request = call.receive<CreateUserRequest>()
        val hash = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

        val user = transaction {
            UserEntity.new {
                username = request.username
                passwordHash = hash
            }
        }

        call.respond(HttpStatusCode.Created, user.toDto())
    }
}

private fun Route.login() {
    post("/login") {
        val request = call.receive<LoginRequest>()
        val user = transaction {
            UserEntity.find { Users.username eq request.username }.singleOrNull()
        }

        if (user == null || !BCrypt.verifyer().verify(request.password.toCharArray(), user.passwordHash).verified) {
            return@post call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }

        val secret = System.getenv("JWT_SECRET")!!
        val issuer = System.getenv("JWT_ISSUER")!!
        val audience = System.getenv("JWT_AUDIENCE")!!
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", user.username)
            .withClaim("userId", user.id.value)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000)) // 1 час
            .sign(Algorithm.HMAC256(secret))

        call.respond(LoginResponse(token))
    }
}
