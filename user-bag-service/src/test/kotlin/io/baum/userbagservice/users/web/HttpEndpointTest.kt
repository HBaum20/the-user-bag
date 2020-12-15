package io.baum.userbagservice.users.web

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.raiseError
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.baum.userbagservice.users.error.RecordNotFoundException
import io.baum.userbagservice.users.tools.hideSensitiveFields
import io.baum.userbagservice.users.web.client.ApiClientFactory
import io.baum.userbagservice.users.web.model.UserModel
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(UserController::class)
class HttpEndpointTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var controller: UserController

    @MockBean
    @Autowired
    private lateinit var service: UserService

    @MockBean
    @Autowired
    private lateinit var apiClientFactory: ApiClientFactory

    @Test
    fun `should create user`() {
        val user = UserModel(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714 282896",
                "password"
        )

        val userString = jacksonObjectMapper().writeValueAsString(user)

        val result = mockMvc.perform(
                post("/users")
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isAccepted)
                .andReturn()
    }

    @Test
    fun `should get user`() {
        val user = UserModel(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714 282896",
                "password"
        )

        val userString = jacksonObjectMapper().writeValueAsString(user)

        `when`(service.getUserById(user.id, user.password)).thenReturn(Either.right(user))

        val result = mockMvc.perform(
                get("/users/1?password=password")
        ).andExpect(status().isOk).andExpect(content().json(userString))
    }

    @Test
    fun `should get all users`() {
        val users = listOf(
                UserModel
                (
                        "1",
                        "Arthur Morgan",
                        8000.0,
                        "arthur.morgan@skybettingandgaming.com",
                        "07714282896",
                        "password"
                ),
                UserModel
                (
                        "2",
                        "Charles Smith",
                        12000.0,
                        "charles.smith@skybettingandgaming.com",
                        "07714 282896",
                        "password"
                )
        )

        val usersString = jacksonObjectMapper().writeValueAsString(users.map { it.hideSensitiveFields() })
        `when`(service.getAllUsers(false)).thenReturn(users)

        val result = mockMvc.perform(
                get("/users")
        ).andExpect(status().isOk).andExpect(content().json(usersString))
    }

    @Test
    fun `should get all local users`() {
        val users = listOf(
                UserModel
                (
                        "1",
                        "Arthur Morgan",
                        8000.0,
                        "arthur.morgan@skybettingandgaming.com",
                        "07714282896",
                        "password"
                ),
                UserModel
                (
                        "2",
                        "Charles Smith",
                        12000.0,
                        "charles.smith@skybettingandgaming.com",
                        "07714 282896",
                        "password"
                )
        )

        val usersString = jacksonObjectMapper().writeValueAsString(users.map { it.hideSensitiveFields() })

        `when`(service.getAllUsers(true)).thenReturn(users)

        val result = mockMvc.perform(
                get("/users/local")
        ).andExpect(status().isOk).andExpect(content().json(usersString))
    }
}