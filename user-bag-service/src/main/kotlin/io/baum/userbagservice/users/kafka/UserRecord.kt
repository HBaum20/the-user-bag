package io.baum.userbagservice.users.kafka

data class UserRecord(
    val id: String,
    val name: String,
    val balance: Double,
    val email: String,
    val phone: String,
    val password: String
)