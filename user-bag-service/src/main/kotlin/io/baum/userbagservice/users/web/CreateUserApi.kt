package io.baum.userbagservice.users.web

import io.baum.userbagservice.users.web.model.UserModel

interface CreateUserApi {
    fun createUser(user: UserModel)
}