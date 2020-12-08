package io.baum.userbagservice.users.kafka.metadata

import io.baum.userbagservice.users.error.KafkaMetadataNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyQueryMetadata
import org.apache.kafka.streams.state.HostInfo
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.config.StreamsBuilderFactoryBean

class MetaDataServiceTest {
    private val streamsBuilderFactoryBean = mockk<StreamsBuilderFactoryBean>()
    private val kafkaStreams = mockk<KafkaStreams>()

    private val kafkaProperties = mockk<KafkaProperties>()

    private val properties = mapOf("application.server" to "localhost:8080")

    private lateinit var underTest: MetaDataService

    private val stringSerializer = Serdes.String().serializer()

    @BeforeEach
    fun setUp() {
        every { streamsBuilderFactoryBean.kafkaStreams } returns kafkaStreams
        every { kafkaProperties.streams.properties } returns properties
        underTest = MetaDataService(streamsBuilderFactoryBean, kafkaProperties)
    }

    @Nested
    inner class MetadataForStoreAndKey {
        @Test
        fun `should return metadata for a store with key`() {
            val streamsMetadata = KeyQueryMetadata(HostInfo("localhost", 8080), emptySet(), 0)
            every { kafkaStreams.queryMetadataForKey(any(), eq("1"), eq(stringSerializer)) } returns streamsMetadata

            val actual = underTest.streamsMetadataForStoreAndKey("encrypted-users-store", "1", stringSerializer)

            assertThat(actual.host).isEqualTo("localhost")
            assertThat(actual.port).isEqualTo(8080)
            assertThat(actual.isThisHost).isEqualTo(true)
        }

        @Test
        fun `should throw kafka metadata exception when it can not find host for store and key`() {
            val streamsMetadata = KeyQueryMetadata(HostInfo("unavailable", -1), emptySet(), 0)
            every { kafkaStreams.queryMetadataForKey(any(), eq("1"), eq(stringSerializer)) } returns streamsMetadata

            assertThatThrownBy { underTest.streamsMetadataForStoreAndKey("encrypted-users-store", "1", stringSerializer) }
                    .isInstanceOf(KafkaMetadataNotFoundException::class.java)
                    .hasMessageContaining("Could not find metadata for store: encrypted-users-store with key 1")
        }
    }
}