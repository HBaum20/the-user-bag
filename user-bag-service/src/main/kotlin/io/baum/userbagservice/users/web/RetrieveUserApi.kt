package io.baum.userbagservice.users.web

import io.baum.userbagservice.users.web.model.UserModel

interface RetrieveUserApi {
    fun getUserById(id: String, password: String): UserModel
    fun getAllUsers(): List<UserModel>
    fun getAllUsersLocal(): List<UserModel>
}