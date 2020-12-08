package io.baum.userbagservice.users.web

import arrow.core.getOrHandle
import io.baum.userbagservice.users.tools.hideSensitiveFields
import io.baum.userbagservice.users.web.client.ApiClientFactory
import io.baum.userbagservice.users.web.model.UserModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val apiClient: ApiClientFactory
) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun createUser(@RequestBody user: UserModel) = userService.createUser(user)

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String, @RequestParam password: String) = userService.getUserById(id, password)
            .getOrHandle { apiClient.forInstance(it).getUserById(id, password) }

    @GetMapping
    fun getAllUsers() = userService.getAllUsers(false).map { it?.hideSensitiveFields() }

    @GetMapping("/remote")
    fun getAllUsersRemote() = userService.getAllUsers(true).map { it?.hideSensitiveFields() }
}