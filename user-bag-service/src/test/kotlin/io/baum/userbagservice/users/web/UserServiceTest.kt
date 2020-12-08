package io.baum.userbagservice.users.web

import arrow.core.Either
import io.baum.userbagservice.users.error.RecordNotFoundException
import io.baum.userbagservice.users.error.UnauthorisedUserException
import io.baum.userbagservice.users.kafka.UserProducer
import io.baum.userbagservice.users.kafka.UserRecord
import io.baum.userbagservice.users.kafka.UserRepository
import io.baum.userbagservice.users.tools.PasswordAuthenticator
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.baum.userbagservice.users.tools.toDomain
import io.baum.userbagservice.users.web.model.UserModel
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val userProducer = mockk<UserProducer>()
    private val passwordAuthenticator = mockk<PasswordAuthenticator>()
    private val underTest = UserService(userProducer, userRepository, passwordAuthenticator)

    @Nested
    inner class GetById {
        @Test
        fun `should return a user when user with that ID exists`() {
            val user = UserRecord(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "hashedPassword"
            )

            every { userRepository.getUserById("1") } returns Either.right(user)
            every { passwordAuthenticator.verifyPassword("password", "hashedPassword") } returns true

            val response = underTest.getUserById("1", "password")

            assertThat(response).isEqualToComparingFieldByField(Either.right(user.toDomain()))
        }

        @Test
        fun `should throw RecordNotFoundException when user repository returns null`() {
            every { userRepository.getUserById("1") } returns Either.right(null)

            assertThrows<RecordNotFoundException> { underTest.getUserById("1", "password") }
        }

        @Test
        fun `should throw UnauthorisedUserException when supplied password is incorrect`() {
            val user = UserRecord(
                    "1",
                    "Arthur Morgan",
                    8000.0,
                    "arthur.morgan@skybettingandgaming.com",
                    "07714282896",
                    "hashedPassword"
            )

            every { userRepository.getUserById("1") } returns Either.right(user)
            every { passwordAuthenticator.verifyPassword("password", "hashedPassword") } returns false

            assertThrows<UnauthorisedUserException> { underTest.getUserById("1", "password") }
        }
    }

    @Nested
    inner class CreateUser {
        @Test
        fun `should call user repository`() {
            val userModel = UserModel(
                    "1",
                    "Arthur Morgan",
                    8000.0,
                    "arthur.morgan@skybettingandgaming.com",
                    "07714282896",
                    "password"
            )

            val userRecord = UserRecord(
                    "1",
                    "Arthur Morgan",
                    8000.0,
                    "arthur.morgan@skybettingandgaming.com",
                    "07714282896",
                    "password"
            )

            every { userProducer.publish("1", userRecord) } returns Unit

            underTest.createUser(userModel)

            verify { userProducer.publish("1", userRecord) }
        }
    }

    @Nested
    inner class GetAllUsers {
        @Test
        fun `should call getAllUsers when remote is false`() {
            val users = listOf(
                    UserModel(
                            "1",
                            "Arthur Morgan",
                            8000.0,
                            "arthur.morgan@skybettingandgaming.com",
                            "07714282896",
                            "password"
                    ),
                    UserModel(
                            "2",
                            "Charles Smith",
                            12000.0,
                            "charles.smith@skybettingandgaming.com",
                            "07714282896",
                            "drowssap"
                    )
            )
            val records = listOf(
                    UserRecord(
                            "1",
                            "Arthur Morgan",
                            8000.0,
                            "arthur.morgan@skybettingandgaming.com",
                            "07714282896",
                            "password"
                    ),
                    UserRecord(
                            "2",
                            "Charles Smith",
                            12000.0,
                            "charles.smith@skybettingandgaming.com",
                            "07714282896",
                            "drowssap"
                    )
            )

            every { userRepository.getAllUsers() } returns records

            val actual = underTest.getAllUsers(false)

            assertThat(actual).isEqualTo(users)

            verify { userRepository.getAllUsers() }
        }

        @Test
        fun `should call getAllUsersLocal() when remote is true and returns users`() {
            val users = listOf(
                    UserModel(
                            "1",
                            "Arthur Morgan",
                            8000.0,
                            "arthur.morgan@skybettingandgaming.com",
                            "07714282896",
                            "password"
                    ),
                    UserModel(
                            "2",
                            "Charles Smith",
                            12000.0,
                            "charles.smith@skybettingandgaming.com",
                            "07714282896",
                            "drowssap"
                    )
            )
            val records = listOf(
                    UserRecord(
                            "1",
                            "Arthur Morgan",
                            8000.0,
                            "arthur.morgan@skybettingandgaming.com",
                            "07714282896",
                            "password"
                    ),
                    UserRecord(
                            "2",
                            "Charles Smith",
                            12000.0,
                            "charles.smith@skybettingandgaming.com",
                            "07714282896",
                            "drowssap"
                    )
            )

            every { userRepository.getAllUsersLocal() } returns records

            val actual = underTest.getAllUsers(true)

            assertThat(actual).isEqualTo(users)

            verify { userRepository.getAllUsersLocal() }
        }
    }
}