package io.baum.userbagservice.users.tools

import org.apache.commons.codec.digest.Crypt
import org.springframework.stereotype.Component

@Component
class PasswordAuthenticator {
    val salt = "hello123"

    fun hash(plaintextPassword: String): String = Crypt.crypt(plaintextPassword, salt)

    fun verifyPassword(plainTextPassword: String, storedHashedPassword: String): Boolean {
        val tmpHashedPassword = Crypt.crypt(plainTextPassword, salt)
        return tmpHashedPassword == storedHashedPassword
    }
}