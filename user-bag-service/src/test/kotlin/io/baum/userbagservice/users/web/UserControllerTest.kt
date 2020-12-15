package io.baum.userbagservice.users.web

import arrow.core.Either
import io.baum.userbagservice.users.tools.hideSensitiveFields
import io.baum.userbagservice.users.web.client.ApiClient
import io.baum.userbagservice.users.web.client.ApiClientFactory
import io.baum.userbagservice.users.web.model.RemoteAddress
import io.baum.userbagservice.users.web.model.UserModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserControllerTest {
    private val userService = mockk<UserService>()
    private val apiClientFactory = mockk<ApiClientFactory>()
    private val apiClient = mockk<ApiClient>()
    private val underTest = UserController(userService, apiClientFactory)

    private val remoteHost = RemoteAddress("localhost", 8081)

    @Nested
    inner class CreateUser {
        @Test
        fun `create user calls user service`() {
            val user = UserModel(
                    "1",
                    "Arthur Morgan",
                    8000.0,
                    "arthur.morgan@skybettingandgaming.com",
                    "07714282896",
                    "password"
            )

            every { userService.createUser(any()) } returns Unit

            underTest.createUser(user)
            verify { userService.createUser(user) }
        }
    }

    @Nested
    inner class GetUserById {
        private val user = UserModel(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "password"
        )
        @Test
        fun `returns user if returned by service`() {
            every { userService.getUserById("1", "password") } returns Either.right(user)

            val result = underTest.getUserById("1", "password")

            assertThat(result).isEqualToComparingFieldByField(user)
            verify { userService.getUserById("1", "password") }
        }

        @Test
        fun `returns user from remote instance if remote address returned by service`() {
            every { apiClientFactory.forInstance(remoteHost) } returns apiClient
            every { apiClient.getUserById("1", "password") } returns user
            every { userService.getUserById("1", "password") } returns Either.left(remoteHost)

            val result = underTest.getUserById("1", "password")

            assertThat(result).isEqualToComparingFieldByField(user)
            verify { apiClient.getUserById("1", "password") }
        }
    }

    @Nested
    inner class GetAll {
        private val users = listOf(
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

        @Test
        fun `get all users should return all users and hide sensitive info`() {
            every { userService.getAllUsers(false) } returns users

            val result = underTest.getAllUsers()

            assertThat(result).isEqualTo(users.map { it.hideSensitiveFields() })
            verify { userService.getAllUsers(false) }
        }

        @Test
        fun `get all users remote should return all users with remote = true and hide sensitive data`() {
            every { userService.getAllUsers(true) } returns users

            val result = underTest.getAllUsersLocal()

            assertThat(result).isEqualTo(users.map { it.hideSensitiveFields() })
            verify { userService.getAllUsers(true) }
        }
    }
}