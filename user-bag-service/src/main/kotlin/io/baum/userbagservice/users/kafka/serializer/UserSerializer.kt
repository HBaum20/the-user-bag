package io.baum.userbagservice.users.kafka.serializer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.baum.userbagservice.users.kafka.UserRecord
import org.apache.kafka.common.serialization.Serializer

class UserSerializer : Serializer<UserRecord> {
    override fun serialize(p0: String?, p1: UserRecord?): ByteArray = jacksonObjectMapper().writeValueAsBytes(p1)
}