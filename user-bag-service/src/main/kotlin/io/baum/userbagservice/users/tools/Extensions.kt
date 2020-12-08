package io.baum.userbagservice.users.tools

import io.baum.userbagservice.users.kafka.UserRecord
import io.baum.userbagservice.users.web.model.UserModel
import org.apache.kafka.streams.state.KeyValueIterator

fun UserModel.toKafkaRecord() = UserRecord(
        id = id,
        name = name,
        balance = balance,
        phone = phone,
        email = email,
        password = password
)

fun UserRecord.toDomain() = UserModel(
        id = id,
        name = name,
        balance = balance,
        phone = phone,
        email = email,
        password = password
)

fun UserModel.hideSensitiveFields() = copy(
        password = this.password.asteriskize(),
        balance = -1.0,
        email = this.email.asteriskize(),
        phone = this.phone.asteriskize()
)


fun String.asteriskize() = replace(Regex("."), "*")

fun <K, V> KeyValueIterator<K, V>.toList() = iterator().asSequence().toList()