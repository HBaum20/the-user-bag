package io.baum.userbagservice.users.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.baum.userbagservice.users.kafka.stream.UserProcessor
import io.baum.userbagservice.users.kafka.stream.UserStream
import io.baum.userbagservice.users.tools.PasswordAuthenticator
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.StreamsBuilder
import org.springframework.context.annotation.Bean
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.stereotype.Component

@Component
class Topology(
        passwordAuthenticator: PasswordAuthenticator,
        builder: StreamsBuilder
) {
    init {
        val userRecordSerde = JsonSerde(UserRecord::class.java, jacksonObjectMapper())
        val userEventStream = UserStream(builder, userRecordSerde).userEventStream()
        UserProcessor(passwordAuthenticator, userEventStream, userRecordSerde).userTable()
    }
}