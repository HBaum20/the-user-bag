package io.baum.userbagservice.users.tools

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.apache.commons.codec.digest.Crypt
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PasswordAuthenticatorTest {

    private val passwordAuthenticator = PasswordAuthenticator()
    private val password = "password"
    private val hashedPassword = "hashedPassword"
    private val salt = "hello123"

    @BeforeEach
    fun setUp() {
        mockkStatic(Crypt::class)
    }

    @Test
    fun `should hash password using apache commons crypt`() {
        every { Crypt.crypt(password, salt) } returns hashedPassword

        val result = passwordAuthenticator.hash(password)

        assertThat(result).isEqualTo(hashedPassword)
        verify { Crypt.crypt(password, salt) }
    }

    @Nested
    inner class VerifyPassword {
        @Test
        fun `verify password should be true with correct password`() {
            every { Crypt.crypt(password, salt) } returns hashedPassword

            val result = passwordAuthenticator.verifyPassword(password, hashedPassword)

            assertThat(result).isTrue()
        }

        @Test
        fun `verify password should be false with wrong password`() {
            val wrongPassword = "wrongPassword"
            val wrongHashedPassword = "wrongHashedPassword"

            every { Crypt.crypt(wrongPassword, salt) } returns wrongHashedPassword

            val result = passwordAuthenticator.verifyPassword(wrongPassword, hashedPassword)

            assertThat(result).isFalse()
        }
    }
}