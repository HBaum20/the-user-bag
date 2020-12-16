package io.baum.userbagservice.users.kafka.metadata

import io.baum.userbagservice.users.error.KafkaMetadataNotFoundException
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.streams.StreamsConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Service

@Service
class MetaDataService(
    private val streamBuilder: StreamsBuilderFactoryBean,
    kafkaProperties: KafkaProperties
) {
    private val applicationServer: List<String>

    private val thisHost: String
    private val thisHostPort: String

    init {
        applicationServer = kafkaProperties.streams.properties[StreamsConfig.APPLICATION_SERVER_CONFIG]?.split(":")
                ?: throw Exception("Unable to find spring.kafka.properties.application.server property")

        thisHost = applicationServer[0]
        thisHostPort = applicationServer[1]
    }

    fun <K> streamsMetadataForStoreAndKey(store: String, key: K, serializer: Serializer<K>): HostStoreInfo {
        val metadataForKey = streamBuilder.kafkaStreams.queryMetadataForKey(store, key, serializer)
        val activeHost = metadataForKey.activeHost

        if(activeHost.host() == "unavailable") {
            throw KafkaMetadataNotFoundException("Could not find metadata for store: $store with key $key")
        }

        val isThisHost = activeHost.host() == thisHost && activeHost.port() == thisHostPort.toInt()

        return HostStoreInfo(
            activeHost.host(),
            activeHost.port(),
            isThisHost
        )
    }
}