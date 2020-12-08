package io.baum.userbagservice.users.kafka.stream

import io.baum.userbagservice.users.kafka.UserRecord
import io.baum.userbagservice.users.tools.PasswordAuthenticator
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.Stores
import org.springframework.context.annotation.Bean

class UserProcessor(
        private val passwordAuthenticator: PasswordAuthenticator,
        private val userEventStream: KStream<String, UserRecord>,
        private val userSerde: Serde<UserRecord>
) {

    @Bean
    fun userTable(): KTable<String, UserRecord> {
        val store = Stores.inMemoryKeyValueStore("encrypted-users-store")
        val stringSerde = Serdes.String()
        return userEventStream
                .mapValues { record ->
                    record.copy(
                            password = passwordAuthenticator.hash(record.password)
                    )
                }.toTable(Materialized.`as`<String, UserRecord>(store)
                        .withKeySerde(stringSerde)
                        .withValueSerde(userSerde))
    }
}