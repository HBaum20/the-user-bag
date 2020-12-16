package io.baum.userbagservice.users.web.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
data class UserModel(
    val id: String,
    val name: String,
    val balance: Double = -1.0,
    val email: String,
    val phone: String,
    val password: String = ""
)