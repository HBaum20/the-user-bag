package io.baum.userbagservice.users.kafka.stream

import io.baum.userbagservice.users.kafka.UserRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.connect.data.SchemaBuilder.string
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.springframework.kafka.support.serializer.JsonSerde

class UserStream(
        private val builder: StreamsBuilder,
        private val userRecordSerde: JsonSerde<UserRecord>
) {
    fun userEventStream(): KStream<String, UserRecord> {
        return builder
                .stream("users-topic", Consumed.with(Serdes.String(), userRecordSerde))
    }
}