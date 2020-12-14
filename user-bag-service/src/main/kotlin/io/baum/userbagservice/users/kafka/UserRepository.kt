package io.baum.userbagservice.users.kafka

import arrow.core.Either
import io.baum.userbagservice.users.kafka.metadata.MetaDataService
import io.baum.userbagservice.users.tools.toKafkaRecord
import io.baum.userbagservice.users.tools.toList
import io.baum.userbagservice.users.web.client.ApiClientFactory
import io.baum.userbagservice.users.web.model.RemoteAddress
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Repository
import org.apache.kafka.streams.KeyValue

@Repository
class UserRepository(
        private val streamsBuilder: StreamsBuilderFactoryBean,
        private val metaDataService: MetaDataService,
        private val apiClient: ApiClientFactory,
        kafkaProperties: KafkaProperties
) {
    private val applicationServer = kafkaProperties.streams.properties[StreamsConfig.APPLICATION_SERVER_CONFIG]?.split(":")
            ?: throw Exception("Unable to find spring.kafka.properties.application.server property")
    private val thisHost = applicationServer[0]
    private val thisPort = applicationServer[1]

    private val keySerializer = Serdes.String().serializer()
    private val encryptedUserStore = ClusteredStateStore<String, UserRecord?>("encrypted-users-store", keySerializer)

    fun getUserById(id: String) = encryptedUserStore.get(id)

    private inner class ClusteredStateStore<K, V>(private val name: String, private val keySerializer: Serializer<K>) {
        val store: ReadOnlyKeyValueStore<K, V> by lazy {
            streamsBuilder.kafkaStreams.store(
                    StoreQueryParameters.fromNameAndType(
                            name,
                            QueryableStoreTypes.keyValueStore<K, V>()
                    ).enableStaleStores()
            )
        }

        fun get(key: K): Either<RemoteAddress, V?> {
            val (host, port, isLocal) = metaDataService.streamsMetadataForStoreAndKey(name, key, keySerializer)

            return if(isLocal) {
                Either.Right(store.get(key))
            } else {
                Either.Left(RemoteAddress(host, port))
            }
        }

        fun getAll(): List<KeyValue<K, V>> {
            return store.all().toList()
        }
    }

    fun getAllUsersLocal(): List<UserRecord?> = encryptedUserStore.getAll().map { it.value }

    fun getAllUsers(): List<UserRecord?> {

        val users = mutableMapOf<String, UserRecord?>()

        encryptedUserStore.getAll().forEach {
            users[it.key] = it.value
        }

        val metadata = streamsBuilder.kafkaStreams.allMetadataForStore("encrypted-users-store")
        val remoteHosts = metadata
                .map { mapOf("host" to it.host(), "port" to it.port().toString()) }
                .filterNot { it["host"] == thisHost && it["port"] == thisPort }

        remoteHosts.forEach { remoteHost ->
            val remoteUserRecords = remoteHost["host"]?.let { remoteHost["port"]?.let { it1 -> getRemoteUsers(it, it1) } }
            remoteUserRecords?.map { (key, value) -> users[key] = value }
        }
        return users.values.toList()
    }

    private fun getRemoteUsers(host: String, port: String): Map<String, UserRecord> =
            apiClient
                    .forInstance(RemoteAddress(host, port.toInt()))
                    .getAllUsers()
                    .map { Pair(it.id, it.toKafkaRecord()) }
                    .toMap()
}