package io.baum.userbagservice.users.kafka.serializer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.baum.userbagservice.users.kafka.UserRecord
import org.apache.kafka.common.serialization.Deserializer

class UserDeserializer : Deserializer<UserRecord> {
    override fun deserialize(p0: String?, p1: ByteArray?): UserRecord = jacksonObjectMapper().readValue(p1, UserRecord::class.java)
}