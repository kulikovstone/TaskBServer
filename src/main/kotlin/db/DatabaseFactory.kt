package com.mkulikov.taskb.server.db

import com.mkulikov.taskb.server.dto.Groups
import com.mkulikov.taskb.server.dto.Tasks
import com.mkulikov.taskb.server.dto.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:5432/taskb_db"
            username = System.getenv("db_username") ?: error("db_username is not set")
            password = System.getenv("db_password") ?: error("db_password is not set")
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Users, Groups, Tasks)
        }
    }
}