package io.baum.userbagservice.users.web

import arrow.core.Either
import io.baum.userbagservice.users.error.RecordNotFoundException
import io.baum.userbagservice.users.error.UnauthorisedUserException
import io.baum.userbagservice.users.kafka.UserProducer
import io.baum.userbagservice.users.kafka.UserRepository
import io.baum.userbagservice.users.tools.PasswordAuthenticator
import io.baum.userbagservice.users.web.model.RemoteAddress
import io.baum.userbagservice.users.web.model.UserModel
import org.springframework.stereotype.Service
import io.baum.userbagservice.users.tools.toDomain
import io.baum.userbagservice.users.tools.toKafkaRecord

@Service
class UserService(
        private val userProducer: UserProducer,
        private val userRepository: UserRepository,
        private val passwordAuthenticator: PasswordAuthenticator
) {
    fun createUser(user: UserModel) =
        userProducer.publish(user.id, user.toKafkaRecord())

    fun getUserById(id: String, password: String): Either<RemoteAddress, UserModel> =
        userRepository.getUserById(id)
                .map { record -> record ?: throw RecordNotFoundException(id) }
                .map { record -> if(passwordAuthenticator.verifyPassword(password, record.password)) record.toDomain() else throw UnauthorisedUserException(id) }

    fun getAllUsers(local: Boolean): List<UserModel> = if(!local) {
        userRepository.getAllUsers().map { it.toDomain() }
    } else {
        userRepository.getAllUsersLocal().map { it.toDomain() }
    }
}